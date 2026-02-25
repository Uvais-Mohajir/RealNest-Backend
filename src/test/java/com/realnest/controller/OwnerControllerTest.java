package com.realnest.controller;

import com.realnest.dto.PropertyDTO;
import com.realnest.enums.PropertyType;
import com.realnest.security.CustomUserDetailsService;
import com.realnest.security.JwtAuthenticationFilter;
import com.realnest.service.OwnerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerController.class)
@AutoConfigureMockMvc(addFilters = false)
class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OwnerService ownerService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final Principal principal = () -> "owner@realnest.com";

    @Test
    void create_shouldReturnCreatedProperty() throws Exception {
        PropertyDTO response = new PropertyDTO();
        response.setId(30L);
        response.setTitle("Owner Listing");
        response.setType(PropertyType.RENT);

        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "img".getBytes());

        when(ownerService.createProperty(eq("owner@realnest.com"), any(PropertyDTO.class), any()))
                .thenReturn(response);

        mockMvc.perform(multipart("/owner/properties")
                        .file(image)
                        .param("title", "Owner Listing")
                        .param("price", "24000")
                        .param("type", "RENT")
                        .param("city", "Mumbai")
                        .param("location", "Andheri")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Property created and sent for approval"))
                .andExpect(jsonPath("$.data.id").value(30L));
    }

    @Test
    void update_shouldReturnUpdatedProperty() throws Exception {
        PropertyDTO response = new PropertyDTO();
        response.setId(31L);
        response.setTitle("Updated");

        when(ownerService.updateProperty(eq("owner@realnest.com"), eq(31L), any(PropertyDTO.class), any()))
                .thenReturn(response);

        mockMvc.perform(multipart("/owner/properties/31")
                        .param("title", "Updated")
                        .param("price", "25000")
                        .param("type", "RENT")
                        .param("city", "Mumbai")
                        .param("location", "Bandra")
                        .principal(principal)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Property updated and sent for re-approval"))
                .andExpect(jsonPath("$.data.id").value(31L));
    }

    @Test
    void delete_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/owner/properties/40").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Property deleted successfully"));
    }

    @Test
    void dashboard_shouldReturnOwnerProperties() throws Exception {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(41L);
        dto.setTitle("Mine");
        when(ownerService.getOwnerProperties("owner@realnest.com")).thenReturn(List.of(dto));

        mockMvc.perform(get("/owner/dashboard").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Owner listings fetched successfully"))
                .andExpect(jsonPath("$.data[0].id").value(41L));
    }
}
