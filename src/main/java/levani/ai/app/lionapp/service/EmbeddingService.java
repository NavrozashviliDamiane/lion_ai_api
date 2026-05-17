package levani.ai.app.lionapp.service;

import jakarta.annotation.PostConstruct;
import levani.ai.app.lionapp.domain.Base;
import levani.ai.app.lionapp.repository.BaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final InMemoryDataCache inMemoryDataCache;
    
    private final Map<Integer, List<Double>> embeddingStore = new ConcurrentHashMap<>();
    private final Map<Integer, String> textStore = new ConcurrentHashMap<>();

    @PostConstruct
    public void initializeEmbeddings() {
        try {
            log.info("Starting embedding initialization from in-memory cache...");
            List<Base> allRecords = inMemoryDataCache.getAllRecords();
            
            if (allRecords.isEmpty()) {
                log.info("No records found in cache to initialize embeddings");
                return;
            }
            
            log.info("Found {} records in cache. Generating embeddings...", allRecords.size());
            
            int successCount = 0;
            int failureCount = 0;
            
            for (Base record : allRecords) {
                try {
                    String textRepresentation = createTextRepresentation(record);
                    List<Double> embedding = generateEmbedding(textRepresentation);
                    
                    if (!embedding.isEmpty()) {
                        embeddingStore.put(record.getId(), embedding);
                        textStore.put(record.getId(), textRepresentation);
                        successCount++;
                    } else {
                        failureCount++;
                    }
                } catch (Exception e) {
                    log.debug("Failed to generate embedding for record {}: {}", record.getId(), e.getMessage());
                    failureCount++;
                }
            }
            
            log.info("Embedding initialization complete. Success: {}, Failed: {}", successCount, failureCount);
            log.info("Total embeddings cached: {}", embeddingStore.size());
            
        } catch (Exception e) {
            log.error("Critical error during embedding initialization: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize embeddings on startup", e);
        }
    }

    @Cacheable(value = "embeddings", key = "#text")
    public List<Double> generateEmbedding(String text) {
        try {
            EmbeddingRequest request = new EmbeddingRequest(List.of(text), null);
            EmbeddingResponse response = embeddingModel.call(request);
            float[] output = response.getResults().get(0).getOutput();
            List<Double> doubleList = new ArrayList<>();
            for (float value : output) {
                doubleList.add((double) value);
            }
            return doubleList;
        } catch (Exception e) {
            log.error("Error generating embedding: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Integer> findSimilarRecords(String query, int topK) {
        List<Double> queryEmbedding = generateEmbedding(query);
        
        if (queryEmbedding.isEmpty()) {
            log.warn("Failed to generate embedding for query: {}", query);
            return Collections.emptyList();
        }

        log.debug("Searching through {} cached embeddings for similar records", embeddingStore.size());
        
        return embeddingStore.entrySet().stream()
                .map(entry -> new SimilarityScore(entry.getKey(), 
                        cosineSimilarity(queryEmbedding, entry.getValue())))
                .sorted(Comparator.comparingDouble(SimilarityScore::score).reversed())
                .limit(topK)
                .map(SimilarityScore::id)
                .collect(Collectors.toList());
    }

    public int getCachedEmbeddingCount() {
        return embeddingStore.size();
    }

    public boolean isCached(Integer recordId) {
        return embeddingStore.containsKey(recordId);
    }

    private String createTextRepresentation(Base record) {
        return String.format(
            "ID: %d, Author: %d, Date: %s, Buyer: %s, VIN: %s, Year: %s, " +
            "Manufacturer: %d, Model: %d, Branch: %s, Parts: %s, " +
            "Auction Pay: %.2f, Dispatch Status: %s, Container: %s",
            record.getId(),
            record.getAuthor(),
            record.getDate(),
            record.getBuyerId(),
            record.getVin(),
            record.getYear(),
            record.getManufacturer(),
            record.getModel(),
            record.getBranch(),
            record.getParts(),
            record.getAuctionPay(),
            record.getDispatchStatus(),
            record.getContainerNumber()
        );
    }

    private double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1.size() != vec2.size()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += vec1.get(i) * vec1.get(i);
            norm2 += vec2.get(i) * vec2.get(i);
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    public void addEmbedding(Base record) {
        String textRepresentation = createTextRepresentation(record);
        List<Double> embedding = generateEmbedding(textRepresentation);
        
        embeddingStore.put(record.getId(), embedding);
        textStore.put(record.getId(), textRepresentation);
    }

    private record SimilarityScore(Integer id, double score) {}
}
