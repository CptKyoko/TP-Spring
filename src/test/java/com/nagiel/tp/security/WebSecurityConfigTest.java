package com.nagiel.tp.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
public class WebSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenAccessPublicApiWithoutAuthentication_thenOk() throws Exception {
        // Test que les routes publiques sont accessibles sans authentification
        mockMvc.perform(get("/api/test/all"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void whenAccessProtectedApiWithoutAuthentication_thenUnauthorized() throws Exception {
        // Test qu'une route protégée requiert une authentification
        mockMvc.perform(get("/api/test/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenAccessProtectedApiWithAuthentication_thenOk() throws Exception {
        // Test qu'une route protégée est accessible avec un utilisateur authentifié
        mockMvc.perform(get("/api/test/user"))
                .andExpect(status().isOk());
    }

    @Test
    public void whenCorsConfigured_thenAllowCorrectOrigins() throws Exception {
        // Test que la configuration CORS est correcte
        mockMvc.perform(MockMvcRequestBuilders.options("/api/articles")
                .header("Origin", "https://localhost")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://localhost"));
    }
}