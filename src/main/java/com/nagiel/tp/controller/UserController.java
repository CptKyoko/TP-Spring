package com.nagiel.tp.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nagiel.tp.model.ERole;
import com.nagiel.tp.model.Role;
import com.nagiel.tp.model.User;
import com.nagiel.tp.payload.request.PutUserRequest;
import com.nagiel.tp.payload.response.MessageResponse;
import com.nagiel.tp.repository.RoleRepository;
import com.nagiel.tp.repository.UserRepository;
import com.nagiel.tp.service.UserService;


@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
    private UserService userService;
	
    @Autowired
    private RoleRepository roleRepository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordEncoder encoder;
    /**
    * Read - Get all users
    * @return - An Iterable object of User full filled
    */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Iterable<User> getUsers() {
        return userService.getUsers();
    }
	
	/**
	 * Read - Get one User 
	 * @param id The id of the User
	 * @return An User object full filled
	 */
	@GetMapping("/user/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or #id == principal.id")
	public User getUser(@PathVariable final Long id) {
		Optional<User> User = userService.getUser(id);
		if(User.isPresent()) {
			return User.get();
		} else {
			return null;
		}
	}
	
	/**
	 * Update - Update an existing User
	 * @param id - The id of the User to update
	 * @param User - The User object updated
	 * @return
	 */
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable final Long id, @RequestBody PutUserRequest user) {
        Optional<User> optionalUser = userService.getUser(id);
        
        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();

            if (user.getUsername() != null) {
        		if (userRepository.existsByUsername(user.getUsername())) {
        			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        		}
                currentUser.setUsername(user.getUsername());
            }

            if (user.getEmail() != null) {
        		if (userRepository.existsByEmail(user.getEmail())) {
        			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        		}
                currentUser.setEmail(user.getEmail());
            }

            if (user.getPassword() != null) {
                currentUser.setPassword(encoder.encode(user.getPassword()));
            }

            if (user.getRole() != null && !user.getRole().isEmpty()) {
            	Set<String> strRoles = user.getRole();
                Set<Role> roles = new HashSet<>();

                if (strRoles == null) {
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                } else {
                    strRoles.forEach(role -> {
                        switch (role) {
                            case "admin":
                                Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                                roles.add(adminRole);
                                break;
                            case "mod":
                                Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                                roles.add(modRole);
                                break;
                            default:
                                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                                roles.add(userRole);
                        }
                    });
                }

                currentUser.setRoles(roles);
            }

            userService.saveUser(currentUser);
            return ResponseEntity.ok(new MessageResponse("User modify successfully!"));
        } else {
            throw new RuntimeException("Utilisateur non trouvé");
        }
    }
	
	
	/**
	 * Delete - Delete an User
	 * @param id - The id of the User to delete
	 */
	@DeleteMapping("/user/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteUser(@PathVariable final Long id) {
		userService.deleteUser(id);
		return ResponseEntity.ok(new MessageResponse("User delete successfully!"));
	}
}
