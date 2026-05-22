package levani.ai.app.lionapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private String response;
    private Integer authorId;
    private String query;
    private LocalDateTime timestamp;
    private boolean success;
    private String errorMessage;
}
