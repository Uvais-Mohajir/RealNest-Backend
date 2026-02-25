package com.realnest.service;

import com.realnest.dto.PropertyDTO;
import com.realnest.enums.PropertyType;

import java.util.List;

public interface CustomerService {
    List<PropertyDTO> searchProperties(String city, PropertyType type, String keyword);
    PropertyDTO getPropertyById(Long id);
}
