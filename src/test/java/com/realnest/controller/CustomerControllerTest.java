package com.realnest.controller;

import com.realnest.dto.PropertyDTO;
import com.realnest.enums.PropertyType;
import com.realnest.security.CustomUserDetailsService;
import com.realnest.security.JwtAuthenticationFilter;
import com.realnest.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void dashboard_shouldReturnData() throws Exception {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(21L);
        dto.setTitle("Customer Property");
        dto.setType(PropertyType.RENT);

        when(customerService.searchProperties("Pune", PropertyType.RENT, "gated"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/customer/dashboard")
                        .param("city", "Pune")
                        .param("type", "RENT")
                        .param("keyword", "gated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Dashboard listings fetched successfully"))
                .andExpect(jsonPath("$.data[0].id").value(21L));
    }

    @Test
    void viewProperty_shouldReturnData() throws Exception {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(22L);
        dto.setTitle("Detail");

        when(customerService.getPropertyById(22L)).thenReturn(dto);

        mockMvc.perform(get("/customer/properties/22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Property detail fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(22L));
    }
}
