package levani.ai.app.lionapp.service;

import levani.ai.app.lionapp.model.CarRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {
    private final DatabaseService databaseService;

    @Cacheable(value = "baseRecords", key = "#authorId")
    public List<CarRecord> getCarRecords(Integer authorId) {
        log.info("Fetching car records for authorId: {} from database", authorId);
        return databaseService.fetchCarRecords(authorId);
    }

    @CacheEvict(value = "baseRecords", key = "#authorId")
    public void invalidateCarRecordsCache(Integer authorId) {
        log.info("Invalidating cache for authorId: {}", authorId);
    }

    @CacheEvict(value = "baseRecords", allEntries = true)
    public void invalidateAllCaches() {
        log.info("Invalidating all caches");
    }

    public String serializeRecordsForContext(List<CarRecord> records) {
        StringBuilder context = new StringBuilder();
        context.append("ხელმისაწვდომი მანქანის ჩანაწერები:\n\n");

        for (int i = 0; i < records.size(); i++) {
            CarRecord record = records.get(i);
            context.append("ჩანაწერი ").append(i + 1).append(":\n");

            record.getData().forEach((key, value) -> {
                if (value != null) {
                    context.append("  ").append(key).append(": ").append(value).append("\n");
                }
            });
            context.append("\n");
        }

        return context.toString();
    }
}
