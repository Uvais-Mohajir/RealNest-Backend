package com.realnest.controller;

import com.realnest.dto.ApiDTO;
import com.realnest.dto.PropertyDTO;
import com.realnest.enums.PropertyType;
import com.realnest.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "Public Properties", description = "Public property browsing APIs")
public class HomeController {
    private final CustomerService customerService;

    @GetMapping("/properties")
    @Operation(summary = "Get properties", description = "Returns approved properties with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Properties fetched successfully")
    })
    public ResponseEntity<ApiDTO<List<PropertyDTO>>> properties(
            @Parameter(description = "Filter by city") 
            @RequestParam(required = false) String city,
            @Parameter(description = "Filter by property type")
            @RequestParam(required = false) PropertyType type,
            @Parameter(description = "Search in title/description")
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(ApiDTO.ok(
                "Properties fetched successfully",
                customerService.searchProperties(city, type, keyword)
        ));
    }

    @GetMapping("/properties/{id}")
    @Operation(summary = "Get property by id", description = "Returns a single approved property detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
    public ResponseEntity<ApiDTO<PropertyDTO>> propertyDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiDTO.ok("Property fetched successfully", customerService.getPropertyById(id)));
    }
}
