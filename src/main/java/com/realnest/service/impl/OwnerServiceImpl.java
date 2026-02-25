package com.realnest.service.impl;

import com.realnest.dto.PropertyDTO;
import com.realnest.entity.Property;
import com.realnest.entity.User;
import com.realnest.enums.PropertyStatus;
import com.realnest.exception.ResourceNotFoundException;
import com.realnest.exception.UnauthorizedException;
import com.realnest.mapper.PropertyMapper;
import com.realnest.repository.PropertyRepository;
import com.realnest.repository.UserRepository;
import com.realnest.service.CloudinaryService;
import com.realnest.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyMapper propertyMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public PropertyDTO createProperty(String ownerEmail, PropertyDTO dto, MultipartFile imageFile) {
        User owner = getOwnerByEmail(ownerEmail);
        Property property = new Property();
        propertyMapper.updateEntity(property, dto);
        property.setOwner(owner);
        property.setStatus(PropertyStatus.PENDING);
        property.setImageUrl(cloudinaryService.uploadImage(imageFile));
        return propertyMapper.toDto(propertyRepository.save(property));
    }

    @Override
    @Transactional
    public PropertyDTO updateProperty(String ownerEmail, Long propertyId, PropertyDTO dto, MultipartFile imageFile) {
        User owner = getOwnerByEmail(ownerEmail);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        assertOwner(owner, property);

        propertyMapper.updateEntity(property, dto);
        if (imageFile != null && !imageFile.isEmpty()) {
            property.setImageUrl(cloudinaryService.uploadImage(imageFile));
        }
        property.setStatus(PropertyStatus.PENDING);
        return propertyMapper.toDto(propertyRepository.save(property));
    }

    @Override
    @Transactional
    public void deleteProperty(String ownerEmail, Long propertyId) {
        User owner = getOwnerByEmail(ownerEmail);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        assertOwner(owner, property);
        propertyRepository.delete(property);
    }

    @Override
    public List<PropertyDTO> getOwnerProperties(String ownerEmail) {
        User owner = getOwnerByEmail(ownerEmail);
        return propertyRepository.findByOwnerId(owner.getId())
                .stream()
                .map(propertyMapper::toDto)
                .toList();
    }

    private User getOwnerByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
    }

    private void assertOwner(User owner, Property property) {
        if (!property.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You are not allowed to modify this property");
        }
    }
}
