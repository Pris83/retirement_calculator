package com.example.retirementCalculator.controller;

import com.example.retirementCalculator.entity.Retirement;
import com.example.retirementCalculator.entity.RetirementResult;
import com.example.retirementCalculator.service.RetirementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling retirement plan calculations.
 * This controller provides an endpoint to calculate retirement planning
 * details based on user input such as age, income, and lifestyle.
 */
@RestController
@RequestMapping("/retirement-plans")
@Validated
@Tag(name = "Retirement Plans", description = "APIs for retirement planning calculations")
public class RetirementController {

    private final RetirementService retirementService;

    /**
     * Constructor for {@code RetirementController}.
     *
     * @param retirementService the service to handle retirement logic
     */
    @Autowired
    public RetirementController(RetirementService retirementService) {
        this.retirementService = retirementService;
    }

    /**
     * Endpoint to calculate a retirement plan based on user-provided data.
     *
     * @param dto a {@link Retirement} DTO containing input parameters like age, salary, and lifestyle
     * @return a {@link RetirementResult} object with calculated retirement details
     */
    @PostMapping(
            value = "/calculate",
            consumes = { "application/json", "application/xml" },
            produces = { "application/json", "application/xml" }
    )
    @Operation(
            summary = "Calculate retirement plan",
            description = "Calculates total amount needed for retirement based on user input",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful calculation",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RetirementResult.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    public ResponseEntity<RetirementResult> calculatePlan(@Valid @RequestBody Retirement dto) {
        RetirementResult result = retirementService.calculatePlan(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/test")
    public ResponseEntity<String> calculatePlan(@RequestBody String dummy) {
        return ResponseEntity.ok("ok");
    }

}
