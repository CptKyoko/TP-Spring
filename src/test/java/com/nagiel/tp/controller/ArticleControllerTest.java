package com.nagiel.tp.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void testGetArticles() throws Exception {
    	// Exécution de la requête GET
    	mockMvc.perform(get("/api/articles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title", is("Titre test 1")))
    		.andExpect(jsonPath("$[0].user.username", is("Guillaume")))
    		.andExpect(jsonPath("$", hasSize(2)));
    }
    
    @Test
    @Order(2)
    @WithMockUser(username = "Guillaume", roles = {"USER"})
    public void testPostArticles() throws Exception {	
        // Données de test
        String requestBody = "{\"title\": \"Test Post\", \"content\": \"Content test\"}";
        
        // Exécution de la requête POST
        mockMvc.perform(post("/api/article")
                .content(requestBody)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Post")) // Vérifie que le titre est correct
                .andExpect(jsonPath("$.content").value("Content test"))
                .andExpect(jsonPath("$.user.username").value("Guillaume"))
                .andExpect(jsonPath("$.date").value(notNullValue()));
        
        // Vérification avec la requête GET
        mockMvc.perform(get("/api/articles"))
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$", hasSize(3)));
    }
    
    @Test
    @Order(3)
    public void testGetArticleById() throws Exception {
    	// Exécution de la requête GET
    	mockMvc.perform(get("/api/article/{id}", 2))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Titre test 2")))
    		.andExpect(jsonPath("$.user.username", is("Guillaume")));
    }
    
    @Test
    @Order(4)
    @WithMockUser(username = "Admin", roles = {"ADMIN"})
    public void testPutArticleWithAdmin() throws Exception {
        // Données de test
        String requestBody = "{\"title\": \"Titre test 1\", \"content\": \"Content test modif by admin\"}";
        
        // Exécution de la requête PUT
        mockMvc.perform(put("/api/article/{id}", 1)
                .content(requestBody)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Titre test 1")))
                .andExpect(jsonPath("$.content").value("Content test modif by admin"))
                .andExpect(jsonPath("$.user.username").value("Guillaume"));

    }
    
    @Test
    @Order(5)
    @WithMockUser(username = "Mod", roles = {"MODERATOR"})
    public void testPutArticleWithModerator() throws Exception {
        // Données de test
        String requestBody = "{\"title\": \"Titre test 1\", \"content\": \"Content test modif by moderator\"}";
        
        // Exécution de la requête PUT
        mockMvc.perform(put("/api/article/{id}", 1)
                .content(requestBody)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Titre test 1")))
                .andExpect(jsonPath("$.content").value("Content test modif by moderator"))
                .andExpect(jsonPath("$.user.username").value("Guillaume"));

    }
    
    @Test
    @Order(6)
    @WithUserDetails("Guillaume")
    public void testPutArticleWithUser() throws Exception {
        // Données de test
        String requestBody = "{\"title\": \"Titre test 1\", \"content\": \"Content test modif by user\"}";
        
        // Exécution de la requête PUT
        mockMvc.perform(put("/api/article/{id}", 1)
                .content(requestBody)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Titre test 1")))
                .andExpect(jsonPath("$.content").value("Content test modif by user"))
                .andExpect(jsonPath("$.user.username").value("Guillaume"));

    }
   
    @Test
    @Order(7)
    @WithMockUser(username = "Admin", roles = {"ADMIN"})
    public void DeleteArticleByAdmin() throws Exception {
    	// Exécution de la requête DELETE
        mockMvc.perform(delete("/api/article/{id}", 2)
                .contentType("application/json"))
                .andExpect(status().isOk());

    }
    
    @Test
    @Order(8)
    @WithUserDetails("Guillaume")
    public void DeleteArticleByUser() throws Exception {
    	// Exécution de la requête DELETE
        mockMvc.perform(delete("/api/article/{id}", 3)
                .contentType("application/json"))
                .andExpect(status().isOk());

    }

}
