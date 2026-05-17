package levani.ai.app.lionapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OpenAiUsageService {

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    private static final String OPENAI_API_BASE = "https://api.openai.com/v1";
    private static final String USAGE_ENDPOINT = OPENAI_API_BASE + "/usage";

    private final RestTemplate restTemplate;

    public OpenAiUsageService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Get OpenAI usage for the current month
     */
    public Map<String, Object> getCurrentMonthUsage() {
        YearMonth currentMonth = YearMonth.now();
        return getUsageForMonth(currentMonth.getYear(), currentMonth.getMonthValue());
    }

    /**
     * Get OpenAI usage for a specific month
     */
    public Map<String, Object> getUsageForMonth(int year, int month) {
        try {
            // OpenAI API requires 'date' parameter in YYYY-MM-DD format
            // We'll use the first day of the month
            String date = String.format("%d-%02d-01", year, month);

            String url = String.format("%s?date=%s", USAGE_ENDPOINT, date);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openAiApiKey);
            headers.set("User-Agent", "LionApp/1.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("month", String.format("%d-%02d", year, month));
                result.put("data", response.getBody());
                result.put("timestamp", System.currentTimeMillis());
                log.info("Successfully retrieved OpenAI usage for {}-{}", year, month);
                return result;
            } else {
                log.error("Failed to retrieve OpenAI usage: {}", response.getStatusCode());
                return createErrorResponse("Failed to retrieve usage data from OpenAI API");
            }

        } catch (Exception e) {
            log.error("Error retrieving OpenAI usage: {}", e.getMessage(), e);
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Get OpenAI account billing information
     */
    public Map<String, Object> getBillingInfo() {
        try {
            String url = OPENAI_API_BASE + "/billing/credit_grants";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openAiApiKey);
            headers.set("User-Agent", "LionApp/1.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("billingInfo", response.getBody());
                result.put("timestamp", System.currentTimeMillis());
                log.info("Successfully retrieved OpenAI billing info");
                return result;
            } else {
                log.error("Failed to retrieve OpenAI billing info: {}", response.getStatusCode());
                return createErrorResponse("Failed to retrieve billing info from OpenAI API");
            }

        } catch (Exception e) {
            log.error("Error retrieving OpenAI billing info: {}", e.getMessage(), e);
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Get OpenAI subscription information
     */
    public Map<String, Object> getSubscriptionInfo() {
        try {
            String url = OPENAI_API_BASE + "/billing/subscription";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openAiApiKey);
            headers.set("User-Agent", "LionApp/1.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("subscription", response.getBody());
                result.put("timestamp", System.currentTimeMillis());
                log.info("Successfully retrieved OpenAI subscription info");
                return result;
            } else {
                log.error("Failed to retrieve OpenAI subscription info: {}", response.getStatusCode());
                return createErrorResponse("Failed to retrieve subscription info from OpenAI API");
            }

        } catch (Exception e) {
            log.error("Error retrieving OpenAI subscription info: {}", e.getMessage(), e);
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Get available OpenAI models
     */
    public Map<String, Object> getAvailableModels() {
        try {
            String url = OPENAI_API_BASE + "/models";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openAiApiKey);
            headers.set("User-Agent", "LionApp/1.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("models", response.getBody());
                result.put("timestamp", System.currentTimeMillis());
                log.info("Successfully retrieved OpenAI models");
                return result;
            } else {
                log.error("Failed to retrieve OpenAI models: {}", response.getStatusCode());
                return createErrorResponse("Failed to retrieve models from OpenAI API");
            }

        } catch (Exception e) {
            log.error("Error retrieving OpenAI models: {}", e.getMessage(), e);
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}
