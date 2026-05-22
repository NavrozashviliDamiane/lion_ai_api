package levani.ai.app.lionapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest {
    private String query;
    private Integer authorId;
    private List<String> relevantFields;
    private String customSystemPrompt;
}
