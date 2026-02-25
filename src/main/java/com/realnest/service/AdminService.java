package com.realnest.service;

import com.realnest.dto.ClosePropertyDTO;
import com.realnest.dto.PropertyClosureDTO;
import com.realnest.dto.PropertyDTO;
import com.realnest.entity.User;

import java.util.List;

public interface AdminService {
    List<User> getAllUsers();
    User updateUser(Long userId, String name, String email);
    void deleteUser(Long userId);
    List<PropertyDTO> getPendingProperties();
    PropertyDTO approveProperty(Long propertyId);
    PropertyClosureDTO closeProperty(Long propertyId, ClosePropertyDTO dto);
    List<PropertyClosureDTO> getClosedProperties();
    void deleteProperty(Long propertyId);
}
