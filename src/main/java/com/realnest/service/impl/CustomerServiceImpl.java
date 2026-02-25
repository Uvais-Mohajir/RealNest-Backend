package com.realnest.service.impl;

import com.realnest.dto.PropertyDTO;
import com.realnest.entity.Property;
import com.realnest.enums.PropertyStatus;
import com.realnest.enums.PropertyType;
import com.realnest.exception.ResourceNotFoundException;
import com.realnest.mapper.PropertyMapper;
import com.realnest.repository.PropertyRepository;
import com.realnest.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;

    @Override
    public List<PropertyDTO> searchProperties(String city, PropertyType type, String keyword) {
        return propertyRepository.searchApproved(city, keyword, type)
                .stream()
                .map(propertyMapper::toDto)
                .toList();
    }

    @Override
    public PropertyDTO getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (property.getStatus() != PropertyStatus.APPROVED) {
            throw new ResourceNotFoundException("Property not found");
        }
        return propertyMapper.toDto(property);
    }
}
