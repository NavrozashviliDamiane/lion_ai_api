package levani.ai.app.lionapp.controller;

import levani.ai.app.lionapp.dto.ChatRequest;
import levani.ai.app.lionapp.dto.ChatResponse;
import levani.ai.app.lionapp.service.ChatAiService;
import levani.ai.app.lionapp.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatAiService chatAiService;
    private final CacheService cacheService;

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(@RequestBody ChatRequest request) {
        log.info("Received chat request for query: '{}' with authorId: {}", request.getQuery(), request.getAuthorId());

        try {
            if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        ChatResponse.builder()
                                .success(false)
                                .errorMessage("Query cannot be empty")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }

            Integer authorId = request.getAuthorId() != null ? request.getAuthorId() : 1748;

            String response;
            if (request.getCustomSystemPrompt() != null && !request.getCustomSystemPrompt().isEmpty()) {
                response = chatAiService.chatWithCustomSystemPrompt(
                        request.getQuery(),
                        authorId,
                        request.getCustomSystemPrompt()
                );
            } else if (request.getRelevantFields() != null && !request.getRelevantFields().isEmpty()) {
                response = chatAiService.chatWithFilteredContext(
                        request.getQuery(),
                        authorId,
                        request.getRelevantFields()
                );
            } else {
                response = chatAiService.chat(request.getQuery(), authorId);
            }

            return ResponseEntity.ok(
                    ChatResponse.builder()
                            .response(response)
                            .query(request.getQuery())
                            .authorId(authorId)
                            .timestamp(LocalDateTime.now())
                            .success(true)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error processing chat request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ChatResponse.builder()
                            .success(false)
                            .errorMessage(e.getMessage())
                            .query(request.getQuery())
                            .authorId(request.getAuthorId())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    @PostMapping("/ask-filtered")
    public ResponseEntity<ChatResponse> askFiltered(@RequestBody ChatRequest request) {
        log.info("Received filtered chat request for query: '{}' with fields: {}", 
                request.getQuery(), request.getRelevantFields());

        try {
            if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        ChatResponse.builder()
                                .success(false)
                                .errorMessage("Query cannot be empty")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }

            if (request.getRelevantFields() == null || request.getRelevantFields().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        ChatResponse.builder()
                                .success(false)
                                .errorMessage("Relevant fields cannot be empty")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }

            Integer authorId = request.getAuthorId() != null ? request.getAuthorId() : 1748;

            String response = chatAiService.chatWithFilteredContext(
                    request.getQuery(),
                    authorId,
                    request.getRelevantFields()
            );

            return ResponseEntity.ok(
                    ChatResponse.builder()
                            .response(response)
                            .query(request.getQuery())
                            .authorId(authorId)
                            .timestamp(LocalDateTime.now())
                            .success(true)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error processing filtered chat request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ChatResponse.builder()
                            .success(false)
                            .errorMessage(e.getMessage())
                            .query(request.getQuery())
                            .authorId(request.getAuthorId())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    @PostMapping("/invalidate-cache/{authorId}")
    public ResponseEntity<String> invalidateCache(@PathVariable Integer authorId) {
        log.info("Invalidating cache for authorId: {}", authorId);
        cacheService.invalidateCarRecordsCache(authorId);
        return ResponseEntity.ok("Cache invalidated for authorId: " + authorId);
    }

    @PostMapping("/invalidate-all-caches")
    public ResponseEntity<String> invalidateAllCaches() {
        log.info("Invalidating all caches");
        cacheService.invalidateAllCaches();
        return ResponseEntity.ok("All caches invalidated");
    }
}
