package com.nagiel.tp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.nagiel.tp.model.ERole;
import com.nagiel.tp.model.Role;
import com.nagiel.tp.model.User;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        // Création d'un utilisateur pour les tests
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");

    }

    @Test
    @Rollback(false)
    public void testFindByUsername() {
        // Arrange
        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByUsername("testUser");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testUser");
    }

    @Test
    @Rollback(false)
    public void testExistsByUsername() {
        // Arrange
        userRepository.save(user);

        // Act
        Boolean exists = userRepository.existsByUsername("testUser");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @Rollback(false)
    public void testNotExistsByUsername() {
        // Act
        Boolean exists = userRepository.existsByUsername("nonExistentUser");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    @Rollback(false)
    public void testExistsByEmail() {
        // Arrange
        userRepository.save(user);

        // Act
        Boolean exists = userRepository.existsByEmail("test@example.com");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @Rollback(false)
    public void testNotExistsByEmail() {
        // Act
        Boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertThat(exists).isFalse();
    }
}
