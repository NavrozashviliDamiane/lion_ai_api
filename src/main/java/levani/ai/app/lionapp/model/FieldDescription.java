package levani.ai.app.lionapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDescription {
    private Integer id;

    @JsonProperty("field_name")
    private String fieldName;

    @JsonProperty("label_name")
    private String labelName;

    @JsonProperty("field_desc")
    private String fieldDesc;

    private String question;
}
