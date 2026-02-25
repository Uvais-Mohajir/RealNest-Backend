package com.realnest.service.impl;

import com.realnest.dto.PropertyDTO;
import com.realnest.entity.Property;
import com.realnest.entity.User;
import com.realnest.enums.PropertyStatus;
import com.realnest.enums.PropertyType;
import com.realnest.enums.Role;
import com.realnest.exception.ResourceNotFoundException;
import com.realnest.exception.UnauthorizedException;
import com.realnest.mapper.PropertyMapper;
import com.realnest.repository.PropertyRepository;
import com.realnest.repository.UserRepository;
import com.realnest.service.CloudinaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PropertyMapper propertyMapper;
    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private OwnerServiceImpl ownerService;

    @Test
    void createProperty_shouldSetPendingStatusAndImageUrl() {
        User owner = User.builder().id(11L).name("Owner").email("owner@realnest.com").role(Role.OWNER).build();
        PropertyDTO request = new PropertyDTO();
        request.setTitle("2BHK Flat");
        request.setPrice(20000.0);
        request.setType(PropertyType.RENT);
        request.setCity("Mumbai");
        request.setLocation("Andheri");

        Property saved = Property.builder()
                .id(5L)
                .title("2BHK Flat")
                .price(20000.0)
                .type(PropertyType.RENT)
                .city("Mumbai")
                .location("Andheri")
                .status(PropertyStatus.PENDING)
                .imageUrl("https://cdn.example/img.jpg")
                .owner(owner)
                .build();

        PropertyDTO response = new PropertyDTO();
        response.setId(5L);
        response.setStatus(PropertyStatus.PENDING);

        when(userRepository.findByEmail("owner@realnest.com")).thenReturn(Optional.of(owner));
        when(cloudinaryService.uploadImage(null)).thenReturn("https://cdn.example/img.jpg");
        when(propertyRepository.save(any(Property.class))).thenReturn(saved);
        when(propertyMapper.toDto(saved)).thenReturn(response);

        PropertyDTO result = ownerService.createProperty("owner@realnest.com", request, null);

        assertEquals(5L, result.getId());
        assertEquals(PropertyStatus.PENDING, result.getStatus());
    }

    @Test
    void updateProperty_shouldThrow_whenPropertyNotFound() {
        User owner = User.builder().id(11L).email("owner@realnest.com").role(Role.OWNER).build();
        when(userRepository.findByEmail("owner@realnest.com")).thenReturn(Optional.of(owner));
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> ownerService.updateProperty("owner@realnest.com", 999L, new PropertyDTO(), null));
    }

    @Test
    void updateProperty_shouldThrow_whenNotOwner() {
        User currentOwner = User.builder().id(11L).email("owner@realnest.com").role(Role.OWNER).build();
        User anotherOwner = User.builder().id(12L).email("other@realnest.com").role(Role.OWNER).build();
        Property property = Property.builder().id(1L).owner(anotherOwner).build();

        when(userRepository.findByEmail("owner@realnest.com")).thenReturn(Optional.of(currentOwner));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        assertThrows(UnauthorizedException.class,
                () -> ownerService.updateProperty("owner@realnest.com", 1L, new PropertyDTO(), null));
    }

    @Test
    void deleteProperty_shouldDelete_whenOwnerMatches() {
        User owner = User.builder().id(11L).email("owner@realnest.com").role(Role.OWNER).build();
        Property property = Property.builder().id(1L).owner(owner).build();

        when(userRepository.findByEmail("owner@realnest.com")).thenReturn(Optional.of(owner));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        ownerService.deleteProperty("owner@realnest.com", 1L);

        verify(propertyRepository).delete(property);
    }

    @Test
    void getOwnerProperties_shouldReturnMappedList() {
        User owner = User.builder().id(11L).email("owner@realnest.com").role(Role.OWNER).build();
        Property property = Property.builder().id(1L).owner(owner).build();
        PropertyDTO dto = new PropertyDTO();
        dto.setId(1L);

        when(userRepository.findByEmail("owner@realnest.com")).thenReturn(Optional.of(owner));
        when(propertyRepository.findByOwnerId(11L)).thenReturn(List.of(property));
        when(propertyMapper.toDto(property)).thenReturn(dto);

        List<PropertyDTO> result = ownerService.getOwnerProperties("owner@realnest.com");

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
    }
}
