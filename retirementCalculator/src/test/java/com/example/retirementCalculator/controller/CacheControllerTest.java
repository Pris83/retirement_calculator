package com.example.retirementCalculator.controller;

import com.example.retirementCalculator.controller.CacheController;
import com.example.retirementCalculator.service.CacheService;
import com.example.retirementCalculator.service.RetirementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CacheController.class)
class CacheControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CacheService cacheService;

    @MockBean
    private RetirementService retirementService;

    @Test
    void testGetCacheStatusSuccess() throws Exception {
        when(cacheService.getCacheStatus("simple")).thenReturn("Redis is UP | Cache key exists");

        mockMvc.perform(get("/cache/status/simple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.cacheStatus").value("Redis is UP | Cache key exists"));
    }

    @Test
    void testGetCacheStatusFailure() throws Exception {
        when(cacheService.getCacheStatus("missing")).thenThrow(new RuntimeException("Redis down"));

        mockMvc.perform(get("/cache/status/missing"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Error fetching cache status: Redis down"));
    }

    @Test
    void testRefreshCacheSuccess() throws Exception {
        when(cacheService.refreshCache("fancy")).thenReturn("4000");

        mockMvc.perform(put("/cache/refresh/fancy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.value").value("4000"));
    }

    @Test
    void testRefreshCacheNoData() throws Exception {
        when(cacheService.refreshCache("unknown")).thenReturn(null);

        mockMvc.perform(put("/cache/refresh/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("No data available to refresh for key: unknown"));
    }

    @Test
    void testRefreshCacheThrowsException() throws Exception {
        when(cacheService.refreshCache("fancy")).thenThrow(new RuntimeException("Redis down"));

        mockMvc.perform(put("/cache/refresh/fancy"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Error refreshing cache: Redis down"));
    }

    @Test
    void testSetCacheSuccess() throws Exception {
        mockMvc.perform(post("/cache/set")
                        .param("key", "luxury")
                        .param("value", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.key").value("luxury"))
                .andExpect(jsonPath("$.value").value("5000"));
    }

    @Test
    void testSetCacheMissingKey() throws Exception {
        mockMvc.perform(post("/cache/set")
                        .param("value", "5000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Key must not be null or empty"));
    }

    @Test
    void testSetCacheMissingValue() throws Exception {
        mockMvc.perform(post("/cache/set")
                        .param("key", "luxury"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Value must not be null or empty"));
    }

    @Test
    void testSetCacheServiceThrowsException() throws Exception {
        doThrow(new RuntimeException("Redis failure")).when(cacheService).updateCache(anyString(), anyString());

        mockMvc.perform(post("/cache/set")
                        .param("key", "luxury")
                        .param("value", "5000"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Error setting cache: Redis failure"));
    }


    @Test
    void testGetCacheHit() throws Exception {
        when(cacheService.fetchFromCache("simple")).thenReturn("3000");

        mockMvc.perform(get("/cache/get/simple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("simple"))
                .andExpect(jsonPath("$.value").value("3000"));
    }

    @Test
    void testGetCacheMiss() throws Exception {
        when(cacheService.fetchFromCache("unknown")).thenReturn(null);

        mockMvc.perform(get("/cache/get/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCacheSuccess() throws Exception {
        mockMvc.perform(delete("/cache/delete/fancy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Cache for 'fancy' deleted successfully"));
    }

    @Test
    void testDeleteCacheThrowsException() throws Exception {
        doThrow(new RuntimeException("Redis failure")).when(cacheService).deleteFromCache("fancy");

        mockMvc.perform(delete("/cache/delete/fancy"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Error deleting cache: Redis failure"));

        verify(cacheService, times(1)).deleteFromCache("fancy");
    }

    @Test
    void testDeleteCacheKeyIsLowercased() throws Exception {
        doNothing().when(cacheService).deleteFromCache("somekey");

        mockMvc.perform(delete("/cache/delete/SomeKey"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        verify(cacheService).deleteFromCache("somekey");
    }


    @Test
    void testRefreshAllCache_shouldReturnSuccessResponse() throws Exception {
        // Given
        String mockResponse = "Cache refreshed successfully for 3 entries.";
        when(cacheService.refreshAllCache()).thenReturn(mockResponse);

        // When + Then
        mockMvc.perform(post("/cache/refreshAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("All cache entries have been refreshed."))
                .andExpect(jsonPath("$.refreshedCache").value(mockResponse));
    }

    @Test
    public void testRefreshAllCache_Failure() throws Exception {
        when(cacheService.refreshAllCache()).thenThrow(new RuntimeException("Redis unavailable"));

        mockMvc.perform(post("/cache/refreshAll"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal server error"))
                .andExpect(jsonPath("$.code").value("RC-500"));

        verify(cacheService, times(1)).refreshAllCache();
    }


}
