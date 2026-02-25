package com.realnest.mapper;

import com.realnest.dto.PropertyDTO;
import com.realnest.entity.Property;
import org.springframework.stereotype.Component;

@Component
public class PropertyMapper {
    public PropertyDTO toDto(Property property) {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setPrice(property.getPrice());
        dto.setType(property.getType());
        dto.setCity(property.getCity());
        dto.setLocation(property.getLocation());
        dto.setImageUrl(property.getImageUrl());
        dto.setStatus(property.getStatus());
        dto.setDateListed(property.getDateListed());
        dto.setOwnerId(property.getOwner().getId());
        dto.setOwnerName(property.getOwner().getName());
        return dto;
    }

    public void updateEntity(Property property, PropertyDTO dto) {
        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setPrice(dto.getPrice());
        property.setType(dto.getType());
        property.setCity(dto.getCity());
        property.setLocation(dto.getLocation());
    }
}
