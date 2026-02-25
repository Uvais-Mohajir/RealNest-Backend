package com.realnest.controller;

import com.realnest.dto.ApiDTO;
import com.realnest.dto.ClosePropertyDTO;
import com.realnest.dto.PropertyClosureDTO;
import com.realnest.dto.PropertyDTO;
import com.realnest.entity.User;
import com.realnest.service.AdminService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin-only moderation and user management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ApiDTO<List<User>>> users() {
        return ResponseEntity.ok(ApiDTO.ok("Users fetched successfully", adminService.getAllUsers()));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiDTO<User>> updateUser(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        return ResponseEntity.ok(ApiDTO.ok(
                "User updated successfully",
                adminService.updateUser(id, name, email)
        ));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiDTO<Void>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiDTO.ok("User deleted successfully", null));
    }

    @GetMapping("/properties/pending")
    @Operation(summary = "Get pending properties")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending properties fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ApiDTO<List<PropertyDTO>>> pending() {
        return ResponseEntity.ok(ApiDTO.ok("Pending properties fetched successfully", adminService.getPendingProperties()));
    }

    @PatchMapping("/properties/{id}/approve")
    @Operation(summary = "Approve property")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property approved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
    public ResponseEntity<ApiDTO<PropertyDTO>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiDTO.ok("Property approved successfully", adminService.approveProperty(id)));
    }

    @PatchMapping("/properties/{id}/close")
    @Operation(summary = "Mark property sold or tenanted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property closure saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Property/User not found")
    })
    public ResponseEntity<ApiDTO<PropertyClosureDTO>> closeProperty(@PathVariable Long id, @Valid @RequestBody ClosePropertyDTO dto) {
        return ResponseEntity.ok(ApiDTO.ok(
                "Property closure saved successfully",
                adminService.closeProperty(id, dto)
        ));
    }

    @GetMapping("/properties/closed")
    @Operation(summary = "Get sold/tenanted properties table data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Closed properties fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ApiDTO<List<PropertyClosureDTO>>> closedProperties() {
        return ResponseEntity.ok(ApiDTO.ok(
                "Closed properties fetched successfully",
                adminService.getClosedProperties()
        ));
    }

    @DeleteMapping("/properties/{id}")
    @Operation(summary = "Delete property")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
    public ResponseEntity<ApiDTO<Void>> deleteProperty(@PathVariable Long id) {
        adminService.deleteProperty(id);
        return ResponseEntity.ok(ApiDTO.ok("Property deleted successfully", null));
    }
}
