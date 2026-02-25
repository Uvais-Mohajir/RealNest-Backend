package com.realnest.dto;

import com.realnest.enums.PropertyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Admin table row for sold/tenanted property")
public class PropertyClosureDTO {
    @Schema(description = "Property id", example = "101")
    private Long propertyId;
    @Schema(description = "Buyer/Tenant user id", example = "12")
    private Long userId;
    @Schema(description = "Final closure price", example = "2500000")
    private Double price;
    @Schema(description = "Property location", example = "Andheri, Mumbai")
    private String location;
    @Schema(description = "Closure type", example = "SOLD")
    private PropertyStatus status;
    @Schema(description = "Closure timestamp")
    private LocalDateTime closedAt;
}
