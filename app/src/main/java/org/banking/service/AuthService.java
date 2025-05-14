package org.banking.service;

import jakarta.transaction.Transactional;
import org.banking.entities.UserInfo;
import org.banking.entities.UserRole;
import org.banking.request.SignupRequestDTO;
import org.banking.repository.UserRepository;
import org.banking.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Transactional
    public String registerUser(SignupRequestDTO signupDto) {
        if (signupDto.getUsername() == null || signupDto.getPassword() == null ||
                signupDto.getEmail() == null || signupDto.getPhone() == null) {
            throw new IllegalArgumentException("Missing required user fields");
        }
    
        String userId = UUID.randomUUID().toString();
        String hashedPassword = passwordEncoder.encode(signupDto.getPassword());

        String roleName = (signupDto.getRole() == null || signupDto.getRole().isEmpty())
                ? "USER"
                : signupDto.getRole().toUpperCase();

        UserRole role = userRoleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        Set<UserRole> roles = new HashSet<>();
        roles.add(role);

        UserInfo user = new UserInfo(userId, signupDto.getUsername(), hashedPassword,
                signupDto.getEmail(), signupDto.getPhone(), 0.0, roles);

        userRepository.save(user);
        return userId;
    }

    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean loginUser(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            return authentication.isAuthenticated();
        } catch (BadCredentialsException e) {
            return false;
        }
    }
}
