package com.nagiel.tp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagiel.tp.model.Role;
import com.nagiel.tp.model.User;
import com.nagiel.tp.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository; 
    
    public Optional<User> getUser(final Long id) {
        return userRepository.findById(id);
    }

    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Optionnel : Retirer les associations manuellement
            for (Role role : user.getRoles()) {
                role.getUsers().remove(user);
            }

            user.getRoles().clear();  // Retirer les rôles de l'utilisateur

            userRepository.delete(user);  // Supprimer l'utilisateur
        } else {
            throw new RuntimeException("Utilisateur non trouvé");
        }
    }

    public User saveUser(User user) {
    	User savedUser = userRepository.save(user);
        return savedUser;
    }
}
