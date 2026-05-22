package levani.ai.app.lionapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import levani.ai.app.lionapp.model.CarRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseService {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public List<CarRecord> fetchCarRecords(Integer authorId) {
        try {
            String query = "CALL PROC_GET_JSON(?)";
            String jsonResult = jdbcTemplate.queryForObject(
                    query,
                    new Object[]{authorId},
                    String.class
            );

            if (jsonResult == null || jsonResult.isEmpty()) {
                log.warn("No data returned from PROC_GET_JSON for authorId: {}", authorId);
                return List.of();
            }

            List<CarRecord> records = objectMapper.readValue(
                    jsonResult,
                    new TypeReference<List<CarRecord>>() {}
            );

            log.info("Successfully fetched {} car records for authorId: {}", records.size(), authorId);
            return records;
        } catch (Exception e) {
            log.error("Error fetching car records from database for authorId: {}", authorId, e);
            throw new RuntimeException("Failed to fetch car records", e);
        }
    }
}
