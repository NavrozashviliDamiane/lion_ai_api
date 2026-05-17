package levani.ai.app.lionapp.controller;

import jakarta.validation.Valid;
import levani.ai.app.lionapp.dto.ChatRequest;
import levani.ai.app.lionapp.dto.ChatResponse;
import levani.ai.app.lionapp.service.AiQueryService;
import levani.ai.app.lionapp.service.ChatSessionService;
import levani.ai.app.lionapp.service.OpenAiUsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ChatController {

    private final AiQueryService aiQueryService;
    private final ChatSessionService chatSessionService;
    private final OpenAiUsageService openAiUsageService;

    @PostMapping("/query")
    public ResponseEntity<ChatResponse> processQuery(@Valid @RequestBody ChatRequest request) {
        log.info("Received chat query: {}", request.getQuery());
        
        ChatResponse response = aiQueryService.processQuery(request);
        
        if (response.getError() != null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<ChatResponse> semanticSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int topK) {
        log.info("Received semantic search query: {}", query);
        
        ChatResponse response = aiQueryService.searchWithEmbeddings(query, topK);
        
        if (response.getError() != null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Chat Service is running");
    }

    @PostMapping("/session/create")
    public ResponseEntity<Map<String, String>> createSession() {
        ChatSessionService.ChatSession session = chatSessionService.createSession();
        log.info("New session created: {}", session.getSessionId());
        return ResponseEntity.ok(Map.of("sessionId", session.getSessionId()));
    }

    @PostMapping("/session/{sessionId}/query")
    public ResponseEntity<ChatResponse> processQueryWithSession(
            @PathVariable String sessionId,
            @Valid @RequestBody ChatRequest request) {
        log.info("Received query for session {}: {}", sessionId, request.getQuery());
        
        ChatSessionService.ChatSession session = chatSessionService.getSession(sessionId);
        boolean sessionWasCreated = false;
        
        // Auto-create session if not found
        if (session == null) {
            log.info("Session {} not found, creating new session", sessionId);
            session = chatSessionService.createSession();
            
            // Update the sessionId to the new one
            sessionId = session.getSessionId();
            sessionWasCreated = true;
            log.info("Created new session: {}", sessionId);
        }

        chatSessionService.addMessageToSession(sessionId, "user", request.getQuery());
        
        String conversationContext = chatSessionService.getSessionContext(sessionId);
        ChatResponse response = aiQueryService.processQueryWithContext(request, conversationContext);
        
        if (response.getError() == null && response.getNaturalLanguageResponse() != null) {
            chatSessionService.addMessageToSession(sessionId, "assistant", response.getNaturalLanguageResponse());
        }
        
        if (response.getError() != null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        // Return response with the (potentially new) session ID in headers
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
                .header("X-Session-Id", sessionId);
                
        if (sessionWasCreated) {
            responseBuilder.header("X-Session-Created", "true");
        }
        
        return responseBuilder.body(response);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<Map<String, Object>> getSession(@PathVariable String sessionId) {
        ChatSessionService.ChatSession session = chatSessionService.getSession(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(Map.of(
                "sessionId", session.getSessionId(),
                "createdAt", session.getCreatedAt(),
                "lastAccessedAt", session.getLastAccessedAt(),
                "language", session.getLanguage(),
                "messageCount", session.getMessages().size()
        ));
    }

    @PostMapping("/session/{sessionId}/language/{language}")
    public ResponseEntity<Map<String, String>> setSessionLanguage(
            @PathVariable String sessionId,
            @PathVariable String language) {
        ChatSessionService.ChatSession session = chatSessionService.getSession(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        
        chatSessionService.setSessionLanguage(sessionId, language);
        return ResponseEntity.ok(Map.of("language", language));
    }

    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable String sessionId) {
        chatSessionService.deleteSession(sessionId);
        log.info("Session deleted: {}", sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sessions/active")
    public ResponseEntity<Map<String, Integer>> getActiveSessionCount() {
        return ResponseEntity.ok(Map.of("activeSessionCount", chatSessionService.getActiveSessionCount()));
    }

    @GetMapping("/openai/usage/current-month")
    public ResponseEntity<Map<String, Object>> getCurrentMonthUsage() {
        log.info("Retrieving OpenAI usage for current month");
        Map<String, Object> usage = openAiUsageService.getCurrentMonthUsage();
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/openai/usage/month/{year}/{month}")
    public ResponseEntity<Map<String, Object>> getMonthUsage(
            @PathVariable int year,
            @PathVariable int month) {
        log.info("Retrieving OpenAI usage for {}-{}", year, month);
        Map<String, Object> usage = openAiUsageService.getUsageForMonth(year, month);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/openai/billing/info")
    public ResponseEntity<Map<String, Object>> getBillingInfo() {
        log.info("Retrieving OpenAI billing info");
        Map<String, Object> billingInfo = openAiUsageService.getBillingInfo();
        return ResponseEntity.ok(billingInfo);
    }

    @GetMapping("/openai/subscription/info")
    public ResponseEntity<Map<String, Object>> getSubscriptionInfo() {
        log.info("Retrieving OpenAI subscription info");
        Map<String, Object> subscriptionInfo = openAiUsageService.getSubscriptionInfo();
        return ResponseEntity.ok(subscriptionInfo);
    }

    @GetMapping("/openai/models")
    public ResponseEntity<Map<String, Object>> getAvailableModels() {
        log.info("Retrieving available OpenAI models");
        Map<String, Object> models = openAiUsageService.getAvailableModels();
        return ResponseEntity.ok(models);
    }
}
