package com.example.retirementCalculator.controller;

import com.example.retirementCalculator.service.CacheService;
import com.example.retirementCalculator.service.RetirementService;
import org.hibernate.engine.internal.CacheHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private static final Logger log = LoggerFactory.getLogger(CacheController.class);
    private final RetirementService retirementService;
    private final CacheService cacheService;

    @Autowired
    public CacheController(RetirementService retirementService, CacheService cacheService) {
        this.retirementService = retirementService;
        this.cacheService = cacheService;
    }

    @PostMapping("/refreshAll")
    public ResponseEntity<Map<String, Object>> refreshAllCache() {
        String refreshedCache = cacheService.refreshAllCache();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "All cache entries have been refreshed.");
        response.put("refreshedCache", refreshedCache); // Keep as Map, not String

        return ResponseEntity.ok(response);
    }


    // Endpoint to get cache status
    @GetMapping("/status/{key}")
    public ResponseEntity<Map<String, String>> getCacheStatus(@PathVariable String key) {
        Map<String, String> response = new HashMap<>();
        try {
            String status = cacheService.getCacheStatus(key);  // Call service method to fetch cache status
            response.put("status", "success");
            response.put("cacheStatus", status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error fetching cache status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Endpoint to manually refresh the cache
    @PutMapping("/refresh/{key}")
    public ResponseEntity<Map<String, String>> refreshCache(@PathVariable String key) {
        Map<String, String> response = new HashMap<>();
        try {
            String refreshedValue = cacheService.refreshCache(key.toLowerCase());

            if (refreshedValue == null) {
                response.put("status", "error");
                response.put("message", "No data available to refresh for key: " + key);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("status", "success");
            response.put("message", "Cache refreshed successfully");
            response.put("key", key.toLowerCase());
            response.put("value", refreshedValue);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error refreshing cache: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/set")
    public ResponseEntity<Map<String, String>> setCache(
            @RequestParam(required = false) String key,
            @RequestParam(required = false) String value
    ) {
        Map<String, String> response = new HashMap<>();

        if (key == null || key.trim().isEmpty()) {
            log.error("Cache set failed: Key is missing or blank");
            response.put("status", "error");
            response.put("message", "Key must not be null or empty");
            return ResponseEntity.badRequest().body(response);
        }

        if (value == null || value.trim().isEmpty()) {
            log.error("Cache set failed: Value is missing or blank");
            response.put("status", "error");
            response.put("message", "Value must not be null or empty");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            cacheService.updateCache(key.toLowerCase(), value);
            log.info("Cache updated: {} = {}", key, value);
            response.put("status", "success");
            response.put("message", "Cache set successfully");
            response.put("key", key.toLowerCase());
            response.put("value", value);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error setting cache for key {}: {}", key, e.getMessage());
            response.put("status", "error");
            response.put("message", "Error setting cache: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/get/{key}")
    public ResponseEntity<Map<String, String>> getCache(@PathVariable String key) {
        String value = cacheService.fetchFromCache(key.toLowerCase());
        if (value == null) {
            log.warn("Cache miss for key: {}", key);
            return ResponseEntity.notFound().build();
        }
        log.info("Cache hit for key: {}, value: {}", key, value);

        Map<String, String> response = new HashMap<>();
        response.put("key", key);
        response.put("value", value);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{key}")
    public ResponseEntity<Map<String, String>> deleteCache(@PathVariable String key) {
        Map<String, String> response = new HashMap<>();
        try {
            cacheService.deleteFromCache(key.toLowerCase());
            log.info("Cache entry deleted for key: {}", key);
            response.put("status", "success");
            response.put("message", "Cache for '" + key + "' deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting cache for key {}: {}", key, e.getMessage());
            response.put("status", "error");
            response.put("message", "Error deleting cache: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
