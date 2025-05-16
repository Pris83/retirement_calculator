package com.example.retirementCalculator.controller;

import com.example.retirementCalculator.entity.Retirement;
import com.example.retirementCalculator.entity.RetirementResult;
import com.example.retirementCalculator.service.RetirementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RetirementController.class)
class RetirementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RetirementService retirementService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void calculatePlanFancy_shouldReturnResult() throws Exception {
        // Given
        Retirement request = new Retirement();
        request.setCurrentAge(30);
        request.setRetirementAge(65);
        request.setInterestRate(5.0);
        request.setLifestyleType("fancy");

        RetirementResult expected = new RetirementResult(
                30,
                65,
                5.0,
                "fancy",
                BigDecimal.valueOf(3000),
                BigDecimal.valueOf(500000)
        );

        Mockito.when(retirementService.calculatePlan(Mockito.any(Retirement.class)))
                .thenReturn(expected);

        // When & Then
        mockMvc.perform(post("/retirement-plans/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentAge").value(30))
                .andExpect(jsonPath("$.retirementAge").value(65))
                .andExpect(jsonPath("$.lifestyleType").value("fancy"));
    }

    @Test
    void calculatePlanSimple_shouldReturnResult() throws Exception {
        // Given
        Retirement request = new Retirement();
        request.setCurrentAge(21);
        request.setRetirementAge(65);
        request.setInterestRate(5.0);
        request.setLifestyleType("simple");

        RetirementResult expected = new RetirementResult(
                21,
                65,
                5.0,
                "simple",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500000)
        );

        Mockito.when(retirementService.calculatePlan(Mockito.any(Retirement.class)))
                .thenReturn(expected);

        // When & Then
        mockMvc.perform(post("/retirement-plans/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentAge").value(21))
                .andExpect(jsonPath("$.retirementAge").value(65))
                .andExpect(jsonPath("$.lifestyleType").value("simple"));
    }

    @Test
    void calculatePlan_shouldReturnBadRequestForInvalidInput() throws Exception {
        // Missing fields (invalid DTO)
        Retirement invalidRequest = new Retirement();

        mockMvc.perform(post("/retirement-plans/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }


}
