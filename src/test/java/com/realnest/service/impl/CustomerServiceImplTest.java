package com.realnest.service.impl;

import com.realnest.dto.PropertyDTO;
import com.realnest.entity.Property;
import com.realnest.entity.User;
import com.realnest.enums.PropertyStatus;
import com.realnest.enums.PropertyType;
import com.realnest.exception.ResourceNotFoundException;
import com.realnest.mapper.PropertyMapper;
import com.realnest.repository.PropertyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private PropertyMapper propertyMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void searchProperties_shouldReturnMappedDtos() {
        User owner = User.builder().id(1L).name("Owner").build();
        Property property = Property.builder()
                .id(10L)
                .title("Flat")
                .status(PropertyStatus.APPROVED)
                .type(PropertyType.RENT)
                .owner(owner)
                .build();
        PropertyDTO dto = new PropertyDTO();
        dto.setId(10L);

        when(propertyRepository.searchApproved("Mumbai", "metro", PropertyType.RENT)).thenReturn(List.of(property));
        when(propertyMapper.toDto(property)).thenReturn(dto);

        List<PropertyDTO> result = customerService.searchProperties("Mumbai", PropertyType.RENT, "metro");

        assertEquals(1, result.size());
        assertEquals(10L, result.getFirst().getId());
    }

    @Test
    void getPropertyById_shouldThrow_whenNotFound() {
        when(propertyRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> customerService.getPropertyById(99L));
    }

    @Test
    void getPropertyById_shouldThrow_whenNotApproved() {
        User owner = User.builder().id(1L).name("Owner").build();
        Property property = Property.builder().id(1L).status(PropertyStatus.PENDING).owner(owner).build();
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        assertThrows(ResourceNotFoundException.class, () -> customerService.getPropertyById(1L));
    }
}
