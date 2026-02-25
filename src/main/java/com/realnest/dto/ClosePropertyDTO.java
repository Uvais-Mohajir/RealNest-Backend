package com.realnest.dto;

import com.realnest.enums.PropertyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request to mark property as SOLD or TENANTED")
public class ClosePropertyDTO {
    @NotNull
    @Schema(description = "Buyer/Tenant user id", example = "12")
    private Long userId;

    @NotNull
    @Schema(description = "Closure type", example = "SOLD", allowableValues = {"SOLD", "TENANTED"})
    private PropertyStatus status;
}
