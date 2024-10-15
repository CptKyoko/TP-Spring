package com.nagiel.tp.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.nagiel.tp.model.ERole;
import com.nagiel.tp.model.Role;
import com.nagiel.tp.model.User;

public class UserDetailsImplTest {

    private User user;
    private Role role;

    @BeforeEach
    public void setUp() {
        // Création d'un rôle pour les tests
        role = new Role();
        role.setName(ERole.ROLE_USER);  // Assurez-vous que ERole est défini

        // Création d'un utilisateur pour les tests
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role)); // Associe un rôle à l'utilisateur
    }

    @Test
    public void testBuildUserDetails() {
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        
        assertNotNull(userDetails);
        assertEquals(user.getId(), userDetails.getId());
        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(user.getEmail(), userDetails.getEmail());
        assertEquals(user.getPassword(), userDetails.getPassword());

        // Vérifie que les rôles sont correctement assignés
        assertEquals(1, userDetails.getAuthorities().size());
        GrantedAuthority authority = new SimpleGrantedAuthority(role.getName().name());
        assertTrue(userDetails.getAuthorities().contains(authority));
    }

    @Test
    public void testGetters() {
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        
        assertEquals(1L, userDetails.getId());
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("test@example.com", userDetails.getEmail());
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    public void testEquals() {
        UserDetailsImpl userDetails1 = UserDetailsImpl.build(user);
        UserDetailsImpl userDetails2 = UserDetailsImpl.build(user);

        assertEquals(userDetails1, userDetails2);
        assertEquals(userDetails1.hashCode(), userDetails2.hashCode());

        UserDetailsImpl differentUserDetails = new UserDetailsImpl(2L, "anotherUser", "other@example.com", "otherPassword", Collections.emptyList());
        assertNotEquals(userDetails1, differentUserDetails);
    }

    @Test
    public void testIsAccountNonExpired() {
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    public void testIsAccountNonLocked() {
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    public void testIsCredentialsNonExpired() {
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    public void testIsEnabled() {
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        assertTrue(userDetails.isEnabled());
    }
}
