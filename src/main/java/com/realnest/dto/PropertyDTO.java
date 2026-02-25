package com.realnest.dto;

import com.realnest.enums.PropertyStatus;
import com.realnest.enums.PropertyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Property details")
public class PropertyDTO {
    @Schema(description = "Property id", example = "101")
    private Long id;

    @NotBlank
    @Schema(description = "Property title", example = "2BHK Apartment")
    private String title;

    @Schema(description = "Property description", example = "Near metro station")
    private String description;

    @NotNull
    @Min(1)
    @Schema(description = "Property price", example = "25000")
    private Double price;

    @NotNull
    @Schema(description = "Listing type", example = "RENT")
    private PropertyType type;

    @NotBlank
    @Schema(description = "City", example = "Mumbai")
    private String city;

    @NotBlank
    @Schema(description = "Area/locality", example = "Andheri")
    private String location;

    @Schema(description = "Property image URL")
    private String imageUrl;
    @Schema(description = "Moderation status")
    private PropertyStatus status;
    @Schema(description = "Owner user id", example = "5")
    private Long ownerId;
    @Schema(description = "Owner name", example = "Ravi Kumar")
    private String ownerName;
    @Schema(description = "Listing timestamp")
    private LocalDateTime dateListed;
}
