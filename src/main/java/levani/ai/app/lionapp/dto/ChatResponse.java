package levani.ai.app.lionapp.dto;

import levani.ai.app.lionapp.domain.Base;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    private String naturalLanguageResponse;
    
    private String generatedQuery;
    
    private List<Base> results;
    
    private Integer resultCount;
    
    private String error;

    public static ChatResponse success(String response, String query, List<Base> results) {
        return ChatResponse.builder()
                .naturalLanguageResponse(response)
                .generatedQuery(query)
                .results(results)
                .resultCount(results.size())
                .build();
    }

    public static ChatResponse error(String errorMessage) {
        return ChatResponse.builder()
                .error(errorMessage)
                .build();
    }
}
