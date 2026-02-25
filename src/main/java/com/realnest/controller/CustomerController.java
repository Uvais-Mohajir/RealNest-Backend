package com.realnest.controller;

import com.realnest.dto.ApiDTO;
import com.realnest.dto.PropertyDTO;
import com.realnest.enums.PropertyType;
import com.realnest.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Customer", description = "Customer-only APIs")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/dashboard")
    @Operation(summary = "Customer dashboard", description = "Returns property listings for customer browsing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard listings fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ApiDTO<List<PropertyDTO>>> dashboard(
            @Parameter(description = "Filter by city")
            @RequestParam(required = false) String city,
            @Parameter(description = "Filter by property type")
            @RequestParam(required = false) PropertyType type,
            @Parameter(description = "Search in title/description")
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(ApiDTO.ok(
                "Dashboard listings fetched successfully",
                customerService.searchProperties(city, type, keyword)
        ));
    }

    @GetMapping("/properties/{id}")
    @Operation(summary = "Get property details", description = "Returns detail of a specific property by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property detail fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
    public ResponseEntity<ApiDTO<PropertyDTO>> viewProperty(@PathVariable Long id) {
        return ResponseEntity.ok(ApiDTO.ok("Property detail fetched successfully", customerService.getPropertyById(id)));
    }
}
