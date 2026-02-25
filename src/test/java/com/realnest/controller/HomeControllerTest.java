package com.realnest.controller;

import com.realnest.dto.PropertyDTO;
import com.realnest.enums.PropertyStatus;
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

@WebMvcTest(HomeController.class)
@AutoConfigureMockMvc(addFilters = false)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void properties_shouldReturnFilteredList() throws Exception {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(10L);
        dto.setTitle("2BHK Apartment");
        dto.setType(PropertyType.RENT);
        dto.setStatus(PropertyStatus.APPROVED);

        when(customerService.searchProperties("Mumbai", PropertyType.RENT, "metro"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/properties")
                        .param("city", "Mumbai")
                        .param("type", "RENT")
                        .param("keyword", "metro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Properties fetched successfully"))
                .andExpect(jsonPath("$.data[0].id").value(10L));
    }

    @Test
    void propertyDetail_shouldReturnPropertyById() throws Exception {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(11L);
        dto.setTitle("Villa");
        dto.setType(PropertyType.SALE);

        when(customerService.getPropertyById(11L)).thenReturn(dto);

        mockMvc.perform(get("/properties/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Property fetched successfully"))
                .andExpect(jsonPath("$.data.title").value("Villa"));
    }
}
