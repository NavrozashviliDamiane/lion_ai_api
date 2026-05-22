package levani.ai.app.lionapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatAiService {
    private final ChatClient chatClient;
    private final RagService ragService;

    public String chat(String userQuery, Integer authorId) {
        log.info("Processing chat query: '{}' for authorId: {}", userQuery, authorId);

        try {
            String context = ragService.buildContextForQuery(userQuery, authorId);

            String response = chatClient
                    .prompt()
                    .user(userQuery)
                    .system(context)
                    .call()
                    .content();

            log.info("Successfully generated response for query: '{}'", userQuery);
            return response;
        } catch (Exception e) {
            log.error("Error processing chat query: '{}'", userQuery, e);
            throw new RuntimeException("Failed to process chat query", e);
        }
    }

    public String chatWithFilteredContext(String userQuery, Integer authorId, List<String> relevantFields) {
        log.info("Processing filtered chat query: '{}' with fields: {}", userQuery, relevantFields);

        try {
            String context = ragService.buildContextForQueryWithFiltering(userQuery, authorId, relevantFields);

            String response = chatClient
                    .prompt()
                    .user(userQuery)
                    .system(context)
                    .call()
                    .content();

            log.info("Successfully generated filtered response for query: '{}'", userQuery);
            return response;
        } catch (Exception e) {
            log.error("Error processing filtered chat query: '{}'", userQuery, e);
            throw new RuntimeException("Failed to process filtered chat query", e);
        }
    }

    public String chatWithCustomSystemPrompt(String userQuery, Integer authorId, String customSystemPrompt) {
        log.info("Processing chat query with custom system prompt: '{}'", userQuery);

        try {
            String context = ragService.buildContextForQuery(userQuery, authorId);
            String fullSystemPrompt = customSystemPrompt + "\n\n" + context;

            String response = chatClient
                    .prompt()
                    .user(userQuery)
                    .system(fullSystemPrompt)
                    .call()
                    .content();

            log.info("Successfully generated response with custom prompt for query: '{}'", userQuery);
            return response;
        } catch (Exception e) {
            log.error("Error processing chat query with custom prompt: '{}'", userQuery, e);
            throw new RuntimeException("Failed to process chat query with custom prompt", e);
        }
    }
}
