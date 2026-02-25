package com.realnest.controller;

import com.realnest.dto.ClosePropertyDTO;
import com.realnest.dto.PropertyClosureDTO;
import com.realnest.dto.PropertyDTO;
import com.realnest.entity.User;
import com.realnest.enums.PropertyStatus;
import com.realnest.enums.Role;
import com.realnest.security.CustomUserDetailsService;
import com.realnest.security.JwtAuthenticationFilter;
import com.realnest.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void users_shouldReturnAllUsers() throws Exception {
        User user = User.builder().id(1L).name("Admin").email("admin@realnest.com").role(Role.ADMIN).build();
        when(adminService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Users fetched successfully"))
                .andExpect(jsonPath("$.data[0].email").value("admin@realnest.com"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        User user = User.builder().id(2L).name("Updated").email("u@realnest.com").role(Role.CUSTOMER).build();
        when(adminService.updateUser(eq(2L), eq("Updated"), eq("u@realnest.com"))).thenReturn(user);
        String json = """
                {"name":"Updated","email":"u@realnest.com"}
                """;

        mockMvc.perform(put("/admin/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.id").value(2L));
    }

    @Test
    void deleteUser_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/admin/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    void pending_shouldReturnPendingProperties() throws Exception {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(7L);
        dto.setTitle("Pending");
        when(adminService.getPendingProperties()).thenReturn(List.of(dto));

        mockMvc.perform(get("/admin/properties/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pending properties fetched successfully"))
                .andExpect(jsonPath("$.data[0].id").value(7L));
    }

    @Test
    void approve_shouldReturnApprovedProperty() throws Exception {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(9L);
        dto.setStatus(PropertyStatus.APPROVED);
        when(adminService.approveProperty(9L)).thenReturn(dto);

        mockMvc.perform(patch("/admin/properties/9/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Property approved successfully"))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }

    @Test
    void closeProperty_shouldReturnClosure() throws Exception {
        String json = """
                {"userId":12,"status":"SOLD"}
                """;

        PropertyClosureDTO response = new PropertyClosureDTO(11L, 12L, 2100000.0, "Andheri", PropertyStatus.SOLD, LocalDateTime.now());
        when(adminService.closeProperty(eq(11L), any(ClosePropertyDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/admin/properties/11/close")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Property closure saved successfully"))
                .andExpect(jsonPath("$.data.propertyId").value(11L));
    }

    @Test
    void closedProperties_shouldReturnRows() throws Exception {
        PropertyClosureDTO row = new PropertyClosureDTO(101L, 12L, 2500000.0, "Andheri, Mumbai", PropertyStatus.SOLD, LocalDateTime.now());
        when(adminService.getClosedProperties()).thenReturn(List.of(row));

        mockMvc.perform(get("/admin/properties/closed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Closed properties fetched successfully"))
                .andExpect(jsonPath("$.data[0].propertyId").value(101L));
    }

    @Test
    void deleteProperty_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/admin/properties/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Property deleted successfully"));
    }
}
