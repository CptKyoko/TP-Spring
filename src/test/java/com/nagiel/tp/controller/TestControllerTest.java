package com.nagiel.tp.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAllAccess() throws Exception {
    	// Exécution de la requête GET
    	mockMvc.perform(get("/api/test/all"))
            .andExpect(status().isOk()) 
            .andExpect(content().string("Public Content."));;
    }
    
    @Test
    @WithMockUser(username = "Guillaume", roles = {"USER"})
    public void testUserAccess() throws Exception {
    	// Exécution de la requête GET
    	mockMvc.perform(get("/api/test/user"))
            .andExpect(status().isOk()) 
            .andExpect(content().string("User Content."));;
    }
    
    @Test
    @WithMockUser(username = "Mod", roles = {"MODERATOR"})
    public void testUserAccessWithModerator() throws Exception {
    	// Exécution de la requête GET
    	mockMvc.perform(get("/api/test/user"))
            .andExpect(status().isOk()) 
            .andExpect(content().string("User Content."));;
    }
    
    @Test
    @WithMockUser(username = "Admin", roles = {"ADMIN"})
    public void testUserAccessWithAdmin() throws Exception {
    	// Exécution de la requête GET
    	mockMvc.perform(get("/api/test/user"))
            .andExpect(status().isOk()) 
            .andExpect(content().string("User Content."));;
    }
    
    @Test
    @WithMockUser(username = "Guest", roles = {})
    public void testUserAccessUnauthorized() throws Exception {
        // Exécution de la requête GET
        mockMvc.perform(get("/api/test/user"))
            .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(username = "Mod", roles = {"MODERATOR"})
    public void testModeratorAccess() throws Exception {
    	// Exécution de la requête GET
    	mockMvc.perform(get("/api/test/mod"))
            .andExpect(status().isOk()) 
            .andExpect(content().string("Moderator Board."));;
    }
    
    @Test
    @WithMockUser(username = "User", roles = {"USER"})
    public void testModeratorAccessUnauthorizedUser() throws Exception {
        // Exécution de la requête GET
        mockMvc.perform(get("/api/test/mod"))
            .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(username = "Admin", roles = {"ADMIN"})
    public void testModeratorAccessUnauthorizedAdmin() throws Exception {
        // Exécution de la requête GET
        mockMvc.perform(get("/api/test/mod"))
            .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(username = "Admin", roles = {"ADMIN"})
    public void testAdminAccess() throws Exception {
    	// Exécution de la requête GET
    	mockMvc.perform(get("/api/test/admin"))
            .andExpect(status().isOk()) 
            .andExpect(content().string("Admin Board."));;
    }
    
    @Test
    @WithMockUser(username = "User", roles = {"USER"})
    public void testAdminAccessUnauthorizedUser() throws Exception {
        // Exécution de la requête GET
        mockMvc.perform(get("/api/test/admin"))
            .andExpect(status().isForbidden()); 
    }
    
    @Test
    @WithMockUser(username = "Mod", roles = {"MODERATOR"})
    public void testAdminAccessUnauthorizedModerator() throws Exception {
        mockMvc.perform(get("/api/test/admin"))
               .andExpect(status().isForbidden());
    }
}
