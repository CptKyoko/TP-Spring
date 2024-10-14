package com.nagiel.tp.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;
    
    @Test
    @WithMockUser(username = "Bob", roles = {"ADMIN"})
    public void testGetUsers() throws Exception {
    	mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username", is("Guillaume")));
    }
    
    @Test
    @WithMockUser(username = "Bob", roles = {"ADMIN"})
    public void testPutUserWithAdmin() throws Exception {
        // Données de test
        String requestBody = "{\"username\": \"Billy\", \"email\": \"a@a.fr\", \"password\": \"newpassword\"}";
        
     // Exécution de la requête PUT
        mockMvc.perform(put("/api/user/{id}", 2)
                .content(requestBody)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Billy"))
                .andExpect(jsonPath("$.password").doesNotExist());

    }
    
    @Test
    @WithUserDetails("Billy")
    public void testPutUserWithUser() throws Exception {
        // Données de test
        String requestBody = "{\"email\": \"billy@billy.fr\"}";
        
     // Exécution de la requête PUT
        mockMvc.perform(put("/api/user/{id}", 2)
                .content(requestBody)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("billy@billy.fr"));

    }
    
    @Test
    @WithMockUser(username = "Bob", roles = {"ADMIN"})
    public void DeleteUser() throws Exception {

     // Exécution de la requête PUT
        mockMvc.perform(delete("/api/user/{id}", 3)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User delete successfully!"));

    }
    
}

