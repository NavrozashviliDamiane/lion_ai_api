package levani.ai.app.lionapp.service;

import jakarta.annotation.PostConstruct;
import levani.ai.app.lionapp.domain.Base;
import levani.ai.app.lionapp.domain.Manufacturer;
import levani.ai.app.lionapp.domain.Model;
import levani.ai.app.lionapp.repository.BaseRepository;
import levani.ai.app.lionapp.repository.ManufacturerRepository;
import levani.ai.app.lionapp.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InMemoryDataCache {

    private final BaseRepository baseRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final ModelRepository modelRepository;
    
    private final Map<Integer, Base> recordCache = new ConcurrentHashMap<>();
    private final Map<Integer, Manufacturer> manufacturerCache = new ConcurrentHashMap<>();
    private final Map<Integer, Model> modelCache = new ConcurrentHashMap<>();
    private List<Base> allRecords = Collections.synchronizedList(new ArrayList<>());
    private volatile boolean initialized = false;

    @PostConstruct
    public void initializeCache() {
        try {
            log.info("Loading manufacturer and model data...");
            loadManufacturersAndModels();
            
            log.info("Loading records for author 2212 from database into memory...");
            List<Base> records = baseRepository.findByAuthor(2212);
            
            if (records.isEmpty()) {
                log.warn("No records found for author 2212");
                initialized = true;
                return;
            }
            
            log.info("Found {} records for author 2212. Enriching with manufacturer and model names...", records.size());
            
            // Enrich records with manufacturer and model names
            for (Base record : records) {
                enrichRecord(record);
                recordCache.put(record.getId(), record);
            }
            
            allRecords = new ArrayList<>(records);
            initialized = true;
            
            log.info("In-memory cache initialized successfully with {} records (author 2212)", recordCache.size());
            
        } catch (Exception e) {
            log.error("Critical error during cache initialization: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize in-memory cache on startup", e);
        }
    }

    private void loadManufacturersAndModels() {
        try {
            List<Manufacturer> manufacturers = manufacturerRepository.findAll();
            for (Manufacturer mfg : manufacturers) {
                manufacturerCache.put(mfg.getId(), mfg);
            }
            log.info("Loaded {} manufacturers", manufacturers.size());
            
            List<Model> models = modelRepository.findAll();
            for (Model model : models) {
                modelCache.put(model.getId(), model);
            }
            log.info("Loaded {} models", models.size());
        } catch (Exception e) {
            log.error("Error loading manufacturers and models: {}", e.getMessage(), e);
        }
    }

    private void enrichRecord(Base record) {
        if (record.getManufacturer() != null) {
            Manufacturer mfg = manufacturerCache.get(record.getManufacturer());
            if (mfg != null) {
                record.setManufacturerName(mfg.getName());
            }
        }
        
        if (record.getModel() != null) {
            Model model = modelCache.get(record.getModel());
            if (model != null) {
                record.setModelName(model.getName());
            }
        }
    }

    public Base getRecordById(Integer id) {
        if (!initialized) {
            throw new RuntimeException("Cache not initialized yet");
        }
        return recordCache.get(id);
    }

    public List<Base> getAllRecords() {
        if (!initialized) {
            throw new RuntimeException("Cache not initialized yet");
        }
        return new ArrayList<>(allRecords);
    }

    public List<Base> searchRecords(String query) {
        if (!initialized) {
            throw new RuntimeException("Cache not initialized yet");
        }
        
        String lowerQuery = query.toLowerCase();
        return allRecords.stream()
                .filter(record -> 
                    (record.getVin() != null && record.getVin().toLowerCase().contains(lowerQuery)) ||
                    (record.getBuyerId() != null && record.getBuyerId().toLowerCase().contains(lowerQuery)) ||
                    (record.getContainerNumber() != null && record.getContainerNumber().toLowerCase().contains(lowerQuery)) ||
                    (record.getBranch() != null && record.getBranch().toLowerCase().contains(lowerQuery)) ||
                    (record.getDispatchStatus() != null && record.getDispatchStatus().toLowerCase().contains(lowerQuery)) ||
                    (record.getManufacturerName() != null && record.getManufacturerName().toLowerCase().contains(lowerQuery)) ||
                    (record.getModelName() != null && record.getModelName().toLowerCase().contains(lowerQuery))
                )
                .collect(Collectors.toList());
    }

    public List<Base> filterByManufacturer(String manufacturerName) {
        if (!initialized) {
            throw new RuntimeException("Cache not initialized yet");
        }
        
        return allRecords.stream()
                .filter(record -> record.getManufacturerName() != null && 
                        record.getManufacturerName().equalsIgnoreCase(manufacturerName))
                .collect(Collectors.toList());
    }

    public List<Base> filterByModel(String modelName) {
        if (!initialized) {
            throw new RuntimeException("Cache not initialized yet");
        }
        
        return allRecords.stream()
                .filter(record -> record.getModelName() != null && 
                        record.getModelName().equalsIgnoreCase(modelName))
                .collect(Collectors.toList());
    }

    public List<Base> filterByAuthor(Integer author) {
        if (!initialized) {
            throw new RuntimeException("Cache not initialized yet");
        }
        
        return allRecords.stream()
                .filter(record -> record.getAuthor().equals(author))
                .collect(Collectors.toList());
    }

    public List<Base> filterByYear(String year) {
        if (!initialized) {
            throw new RuntimeException("Cache not initialized yet");
        }
        
        return allRecords.stream()
                .filter(record -> record.getYear().equals(year))
                .collect(Collectors.toList());
    }

    public List<Base> filterByDispatchStatus(String status) {
        if (!initialized) {
            throw new RuntimeException("Cache not initialized yet");
        }
        
        return allRecords.stream()
                .filter(record -> record.getDispatchStatus() != null && 
                        record.getDispatchStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public List<Base> filterByAuctionPayRange(Double minPay, Double maxPay) {
        if (!initialized) {
            throw new RuntimeException("Cache not initialized yet");
        }
        
        return allRecords.stream()
                .filter(record -> record.getAuctionPay() >= minPay && record.getAuctionPay() <= maxPay)
                .collect(Collectors.toList());
    }

    public int getCacheSize() {
        return recordCache.size();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void refreshCache() {
        log.info("Refreshing in-memory cache...");
        recordCache.clear();
        allRecords.clear();
        initialized = false;
        initializeCache();
    }
}
