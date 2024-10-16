package com.nagiel.tp.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthEntryPointJwtTest {

    private final AuthEntryPointJwt authEntryPoint = new AuthEntryPointJwt();

//    @Test
//    public void commence_shouldReturnUnauthorizedResponse() throws Exception {
//        // Mock des objets HttpServletRequest et HttpServletResponse
//        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
//        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
//        AuthenticationException authException = Mockito.mock(AuthenticationException.class);
//
//        // Configuration du comportement du mock
//        when(request.getServletPath()).thenReturn("/api/test/admin");
//        when(authException.getMessage()).thenReturn("Full authentication is required.");
//
//        // Créer un ByteArrayOutputStream pour capturer le output stream
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        PrintWriter writer = new PrintWriter(outputStream);
//
//        // Configuration du mock pour renvoyer le PrintWriter
//        when(response.getWriter()).thenReturn(writer);
//
//        // Appel de la méthode à tester
//        authEntryPoint.commence(request, response, authException);
//
//        // Vérification des interactions avec le mock
//        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
//        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//
//        // Assurez-vous d'appeler flush() pour écrire les données dans le ByteArrayOutputStream
//        writer.flush();
//
//        // Vérifiez le contenu écrit dans le output stream
//        String jsonResponse = outputStream.toString();
//        System.out.println(jsonResponse); // Affiche la réponse JSON pour vérification
//
//        // Assertions sur le contenu JSON
//        assert jsonResponse.contains("\"status\":401");
//        assert jsonResponse.contains("\"error\":\"Unauthorized\"");
//        assert jsonResponse.contains("\"message\":\"Full authentication is required.\"");
//        assert jsonResponse.contains("\"path\":\"/api/test\"");
//    }
}
