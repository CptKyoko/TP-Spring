package com.nagiel.tp.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagiel.tp.payload.request.LoginRequest;
import com.nagiel.tp.payload.request.SignupRequest;
import com.nagiel.tp.payload.response.JwtResponse;
import com.nagiel.tp.payload.response.MessageResponse;
import com.nagiel.tp.payload.response.UserInfoResponse;
import com.nagiel.tp.repository.RoleRepository;
import com.nagiel.tp.repository.UserRepository;
import com.nagiel.tp.security.jwt.JwtUtils;
import com.nagiel.tp.service.UserDetailsImpl;
import com.nagiel.tp.model.ERole;
import com.nagiel.tp.model.Role;
import com.nagiel.tp.model.User;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authentification de l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Mettre à jour le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Récupérer les détails de l'utilisateur
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Générer le Bearer Token
            String jwt = jwtUtils.generateTokenFromUsername(userDetails.getUsername());

            // Récupérer les rôles de l'utilisateur
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // Retourner le token dans la réponse avec les informations de l'utilisateur
            return ResponseEntity.ok(new JwtResponse(jwt, new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Créer un nouvel utilisateur
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
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

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    
    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        // Ici, vous pouvez simplement retourner un message indiquant que l'utilisateur est déconnecté.
        return ResponseEntity.ok(new MessageResponse("You've been signed out!"));
    }

}
