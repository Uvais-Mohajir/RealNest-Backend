package com.realnest.service.impl;

import com.realnest.dto.ClosePropertyDTO;
import com.realnest.dto.PropertyClosureDTO;
import com.realnest.dto.PropertyDTO;
import com.realnest.entity.Property;
import com.realnest.entity.PropertyClosure;
import com.realnest.entity.User;
import com.realnest.enums.PropertyStatus;
import com.realnest.exception.ResourceNotFoundException;
import com.realnest.exception.UnauthorizedException;
import com.realnest.mapper.PropertyMapper;
import com.realnest.repository.PropertyClosureRepository;
import com.realnest.repository.PropertyRepository;
import com.realnest.repository.UserRepository;
import com.realnest.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyClosureRepository propertyClosureRepository;
    private final PropertyMapper propertyMapper;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateUser(Long userId, String name, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (name != null && !name.isBlank()) {
            user.setName(name);
        }
        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public List<PropertyDTO> getPendingProperties() {
        return propertyRepository.findByStatus(PropertyStatus.PENDING)
                .stream()
                .map(propertyMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public PropertyDTO approveProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        property.setStatus(PropertyStatus.APPROVED);
        return propertyMapper.toDto(propertyRepository.save(property));
    }

    @Override
    @Transactional
    public PropertyClosureDTO closeProperty(Long propertyId, ClosePropertyDTO dto) {
        if (dto.getStatus() != PropertyStatus.SOLD && dto.getStatus() != PropertyStatus.TENANTED) {
            throw new UnauthorizedException("Status must be SOLD or TENANTED");
        }

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PropertyClosure closure = propertyClosureRepository.findByPropertyId(propertyId)
                .orElseGet(PropertyClosure::new);
        closure.setProperty(property);
        closure.setUser(user);
        closure.setPrice(property.getPrice());
        closure.setLocation(property.getLocation());
        closure.setClosureType(dto.getStatus());

        property.setStatus(dto.getStatus());
        propertyRepository.save(property);

        PropertyClosure saved = propertyClosureRepository.save(closure);
        return new PropertyClosureDTO(
                saved.getProperty().getId(),
                saved.getUser().getId(),
                saved.getPrice(),
                saved.getLocation(),
                saved.getClosureType(),
                saved.getClosedAt()
        );
    }

    @Override
    public List<PropertyClosureDTO> getClosedProperties() {
        return propertyClosureRepository.findAll().stream()
                .map(c -> new PropertyClosureDTO(
                        c.getProperty().getId(),
                        c.getUser().getId(),
                        c.getPrice(),
                        c.getLocation(),
                        c.getClosureType(),
                        c.getClosedAt()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void deleteProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        propertyRepository.delete(property);
    }
}
