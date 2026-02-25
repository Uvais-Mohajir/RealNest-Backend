package com.realnest.controller;

import com.realnest.dto.ApiDTO;
import com.realnest.dto.PropertyDTO;
import com.realnest.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
@Tag(name = "Owner", description = "Owner-only property management APIs")
@SecurityRequirement(name = "bearerAuth")
public class OwnerController {
    private final OwnerService ownerService;

    @PostMapping(value = "/properties", consumes = {"multipart/form-data"})
    @Operation(summary = "Create property", description = "Creates a new property and marks it pending approval")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property created"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ApiDTO<PropertyDTO>> create(
            @Valid @ModelAttribute PropertyDTO dto,
            @RequestParam(required = false) MultipartFile image,
            Principal principal
    ) {
        return ResponseEntity.ok(ApiDTO.ok(
                "Property created and sent for approval",
                ownerService.createProperty(principal.getName(), dto, image)
        ));
    }

    @PutMapping(value = "/properties/{id}", consumes = {"multipart/form-data"})
    @Operation(summary = "Update property", description = "Updates owner property and marks it for re-approval")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property updated"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
    public ResponseEntity<ApiDTO<PropertyDTO>> update(
            @PathVariable Long id,
            @Valid @ModelAttribute PropertyDTO dto,
            @RequestParam(required = false) MultipartFile image,
            Principal principal
    ) {
        return ResponseEntity.ok(ApiDTO.ok(
                "Property updated and sent for re-approval",
                ownerService.updateProperty(principal.getName(), id, dto, image)
        ));
    }

    @DeleteMapping("/properties/{id}")
    @Operation(summary = "Delete property", description = "Deletes a property owned by logged-in owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
    public ResponseEntity<ApiDTO<Void>> delete(@PathVariable Long id, Principal principal) {
        ownerService.deleteProperty(principal.getName(), id);
        return ResponseEntity.ok(ApiDTO.ok("Property deleted successfully", null));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Owner dashboard", description = "Returns properties owned by logged-in owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Owner listings fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ApiDTO<List<PropertyDTO>>> dashboard(Principal principal) {
        return ResponseEntity.ok(ApiDTO.ok(
                "Owner listings fetched successfully",
                ownerService.getOwnerProperties(principal.getName())
        ));
    }
}
