package com.realnest.service;

import com.realnest.dto.PropertyDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OwnerService {
    PropertyDTO createProperty(String ownerEmail, PropertyDTO dto, MultipartFile imageFile);
    PropertyDTO updateProperty(String ownerEmail, Long propertyId, PropertyDTO dto, MultipartFile imageFile);
    void deleteProperty(String ownerEmail, Long propertyId);
    List<PropertyDTO> getOwnerProperties(String ownerEmail);
}
