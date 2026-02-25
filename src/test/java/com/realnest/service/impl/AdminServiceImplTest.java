package com.realnest.service.impl;

import com.realnest.dto.ClosePropertyDTO;
import com.realnest.dto.PropertyClosureDTO;
import com.realnest.dto.PropertyDTO;
import com.realnest.entity.Property;
import com.realnest.entity.PropertyClosure;
import com.realnest.entity.User;
import com.realnest.enums.PropertyStatus;
import com.realnest.enums.PropertyType;
import com.realnest.enums.Role;
import com.realnest.exception.ResourceNotFoundException;
import com.realnest.exception.UnauthorizedException;
import com.realnest.mapper.PropertyMapper;
import com.realnest.repository.PropertyClosureRepository;
import com.realnest.repository.PropertyRepository;
import com.realnest.repository.UserRepository;
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
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private PropertyClosureRepository propertyClosureRepository;
    @Mock
    private PropertyMapper propertyMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void getAllUsers_shouldReturnAll() {
        when(userRepository.findAll()).thenReturn(List.of(User.builder().id(1L).role(Role.ADMIN).build()));
        assertEquals(1, adminService.getAllUsers().size());
    }

    @Test
    void updateUser_shouldUpdateFields() {
        User user = User.builder().id(2L).name("Old").email("old@x.com").role(Role.CUSTOMER).build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = adminService.updateUser(2L, "New", "new@x.com");

        assertEquals("New", updated.getName());
        assertEquals("new@x.com", updated.getEmail());
    }

    @Test
    void updateUser_shouldThrow_whenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> adminService.updateUser(99L, "n", "e"));
    }

    @Test
    void deleteUser_shouldDeleteFoundUser() {
        User user = User.builder().id(3L).build();
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        adminService.deleteUser(3L);
        verify(userRepository).delete(user);
    }

    @Test
    void getPendingProperties_shouldReturnMapped() {
        User owner = User.builder().id(1L).name("Owner").build();
        Property property = Property.builder().id(10L).status(PropertyStatus.PENDING).owner(owner).build();
        PropertyDTO dto = new PropertyDTO();
        dto.setId(10L);

        when(propertyRepository.findByStatus(PropertyStatus.PENDING)).thenReturn(List.of(property));
        when(propertyMapper.toDto(property)).thenReturn(dto);

        List<PropertyDTO> result = adminService.getPendingProperties();
        assertEquals(10L, result.getFirst().getId());
    }

    @Test
    void approveProperty_shouldSetApproved() {
        User owner = User.builder().id(1L).name("Owner").build();
        Property property = Property.builder().id(10L).owner(owner).status(PropertyStatus.PENDING).build();
        PropertyDTO dto = new PropertyDTO();
        dto.setStatus(PropertyStatus.APPROVED);

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(propertyMapper.toDto(any(Property.class))).thenReturn(dto);

        PropertyDTO result = adminService.approveProperty(10L);
        assertEquals(PropertyStatus.APPROVED, result.getStatus());
    }

    @Test
    void closeProperty_shouldThrow_whenStatusInvalid() {
        ClosePropertyDTO dto = new ClosePropertyDTO();
        dto.setUserId(1L);
        dto.setStatus(PropertyStatus.PENDING);
        assertThrows(UnauthorizedException.class, () -> adminService.closeProperty(1L, dto));
    }

    @Test
    void closeProperty_shouldSaveClosureAndUpdateProperty() {
        User owner = User.builder().id(1L).name("Owner").role(Role.OWNER).build();
        Property property = Property.builder()
                .id(10L)
                .price(2000000.0)
                .location("Andheri")
                .type(PropertyType.SALE)
                .status(PropertyStatus.APPROVED)
                .owner(owner)
                .build();
        User customer = User.builder().id(12L).name("Customer").role(Role.CUSTOMER).build();
        ClosePropertyDTO dto = new ClosePropertyDTO();
        dto.setUserId(12L);
        dto.setStatus(PropertyStatus.SOLD);

        PropertyClosure closure = PropertyClosure.builder().property(property).user(customer).price(2000000.0).location("Andheri").closureType(PropertyStatus.SOLD).build();

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(userRepository.findById(12L)).thenReturn(Optional.of(customer));
        when(propertyClosureRepository.findByPropertyId(10L)).thenReturn(Optional.empty());
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(propertyClosureRepository.save(any(PropertyClosure.class))).thenReturn(closure);

        PropertyClosureDTO result = adminService.closeProperty(10L, dto);

        assertEquals(10L, result.getPropertyId());
        assertEquals(PropertyStatus.SOLD, result.getStatus());
    }
}
