package levani.ai.app.lionapp.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ChatSessionService {

    @Getter
    public static class ChatMessage {
        private final String role;
        private final String content;
        private final LocalDateTime timestamp;

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
            this.timestamp = LocalDateTime.now();
        }
    }

    @Getter
    public static class ChatSession {
        private final String sessionId;
        private final List<ChatMessage> messages;
        private final LocalDateTime createdAt;
        private LocalDateTime lastAccessedAt;
        private String language;

        public ChatSession(String sessionId) {
            this.sessionId = sessionId;
            this.messages = Collections.synchronizedList(new ArrayList<>());
            this.createdAt = LocalDateTime.now();
            this.lastAccessedAt = LocalDateTime.now();
            this.language = "en";
        }

        public void addMessage(String role, String content) {
            messages.add(new ChatMessage(role, content));
            this.lastAccessedAt = LocalDateTime.now();
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public List<ChatMessage> getMessages() {
            this.lastAccessedAt = LocalDateTime.now();
            return new ArrayList<>(messages);
        }

        public String getConversationContext() {
            StringBuilder context = new StringBuilder();
            for (ChatMessage msg : messages) {
                context.append(msg.role).append(": ").append(msg.content).append("\n");
            }
            return context.toString();
        }
    }

    private final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();
    private static final long SESSION_TIMEOUT_MINUTES = 60;

    public ChatSession createSession() {
        String sessionId = UUID.randomUUID().toString();
        ChatSession session = new ChatSession(sessionId);
        sessions.put(sessionId, session);
        log.info("Created new chat session: {}", sessionId);
        return session;
    }

    public ChatSession getSession(String sessionId) {
        ChatSession session = sessions.get(sessionId);
        if (session != null) {
            session.lastAccessedAt = LocalDateTime.now();
            return session;
        }
        log.warn("Session not found: {}", sessionId);
        return null;
    }

    public void addMessageToSession(String sessionId, String role, String content) {
        ChatSession session = getSession(sessionId);
        if (session != null) {
            session.addMessage(role, content);
            log.debug("Added message to session {}: {} - {}", sessionId, role, content.substring(0, Math.min(50, content.length())));
        } else {
            log.warn("Cannot add message - session not found: {}", sessionId);
        }
    }

    public String getSessionContext(String sessionId) {
        ChatSession session = getSession(sessionId);
        if (session != null) {
            return session.getConversationContext();
        }
        return "";
    }

    public void setSessionLanguage(String sessionId, String language) {
        ChatSession session = getSession(sessionId);
        if (session != null) {
            session.setLanguage(language);
            log.debug("Set language for session {}: {}", sessionId, language);
        }
    }

    public String getSessionLanguage(String sessionId) {
        ChatSession session = getSession(sessionId);
        if (session != null) {
            return session.getLanguage();
        }
        return "en";
    }

    public void deleteSession(String sessionId) {
        sessions.remove(sessionId);
        log.info("Deleted chat session: {}", sessionId);
    }

    public void cleanupExpiredSessions() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(SESSION_TIMEOUT_MINUTES);
        sessions.entrySet().removeIf(entry -> entry.getValue().getLastAccessedAt().isBefore(expirationTime));
        log.info("Cleaned up expired sessions");
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }

    public List<String> getAllSessionIds() {
        return new ArrayList<>(sessions.keySet());
    }
}
