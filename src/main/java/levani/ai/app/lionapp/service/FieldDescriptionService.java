package levani.ai.app.lionapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import levani.ai.app.lionapp.model.FieldDescription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FieldDescriptionService {
    private final ObjectMapper objectMapper;
    private volatile Map<String, FieldDescription> fieldDescriptionMap;

    public synchronized Map<String, FieldDescription> loadFieldDescriptions() {
        if (fieldDescriptionMap != null) {
            return fieldDescriptionMap;
        }

        try {
            ClassPathResource resource = new ClassPathResource("field_descriptions.json");
            List<FieldDescription> descriptions = objectMapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<List<FieldDescription>>() {}
            );

            fieldDescriptionMap = new HashMap<>();
            for (FieldDescription desc : descriptions) {
                fieldDescriptionMap.put(desc.getFieldName(), desc);
            }

            log.info("Successfully loaded {} field descriptions", fieldDescriptionMap.size());
            return fieldDescriptionMap;
        } catch (IOException e) {
            log.error("Error loading field descriptions from JSON file", e);
            throw new RuntimeException("Failed to load field descriptions", e);
        }
    }

    public FieldDescription getFieldDescription(String fieldName) {
        Map<String, FieldDescription> descriptions = loadFieldDescriptions();
        return descriptions.get(fieldName);
    }

    public String buildFieldContextString() {
        Map<String, FieldDescription> descriptions = loadFieldDescriptions();
        StringBuilder context = new StringBuilder();
        context.append("ხელმისაწვდომი ველები და მათი აღწერა:\n\n");

        for (FieldDescription desc : descriptions.values()) {
            context.append("ველი: ").append(desc.getFieldName()).append("\n");
            context.append("ლეიბელი: ").append(desc.getLabelName()).append("\n");
            context.append("აღწერა: ").append(desc.getFieldDesc()).append("\n");
            if (desc.getQuestion() != null && !desc.getQuestion().isEmpty()) {
                context.append("მაგალითი კითხვები: ").append(desc.getQuestion()).append("\n");
            }
            context.append("\n");
        }

        return context.toString();
    }
}
