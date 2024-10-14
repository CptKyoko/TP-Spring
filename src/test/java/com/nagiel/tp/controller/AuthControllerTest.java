package com.nagiel.tp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.nagiel.tp.security.jwt.JwtUtils;
import com.nagiel.tp.service.UserDetailsImpl;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    public void testRegisterUser() throws Exception {
        String requestBody = "{\"username\": \"Bob\", \"email\": \"b@b.fr\", \"password\": \"password\"}";

        mockMvc.perform(post("/api/auth/signup")
                .content(requestBody)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }
    

    @Test
    public void testAuthenticateUser() throws Exception {
        String username = "Bob";
        String password = "password";

        // Crée un UserDetailsImpl avec les bons paramètres
        UserDetailsImpl userDetails = new UserDetailsImpl(3L, username, "b@b.fr", password, new ArrayList<>());

        // Simuler l'authentification avec AuthenticationManager
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities()));

        // Spécifier l'utilisateur principal dans le contexte de sécurité
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities())
        );

        // Configurer le JwtUtils pour renvoyer un cookie JWT simulé
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "dummy-jwt-token").path("/").httpOnly(true).build();
        when(jwtUtils.generateJwtCookie(any(UserDetailsImpl.class))).thenReturn(jwtCookie);

        // Effectuer une requête HTTP POST avec les informations de connexion
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk()) // Vérifie le statut de la réponse
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Vérifie le type de média de la réponse
                .andExpect(jsonPath("$.username").value(username)) // Vérifie les informations de l'utilisateur dans la réponse JSON
                .andExpect(jsonPath("$.email").value("b@b.fr"))
                .andExpect(jsonPath("$.roles").isArray()) // Vérifie que les rôles sont renvoyés en tant qu'un tableau
                .andExpect(header().exists(HttpHeaders.SET_COOKIE)) // Vérifie que l'en-tête Set-Cookie est défini
                .andExpect(header().string(HttpHeaders.SET_COOKIE, jwtCookie.toString())); // Vérifie le contenu du cookie JWT dans l'en-tête Set-Cookie
    }

}
