package org.banking.service;

import org.banking.entities.UserInfo;
import org.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public boolean signUpUser(UserInfo userInfo) {

        if (userRepository.findByUsername(userInfo.getUsername()) != null) {
            return false;
        }

        String hashedPassword = passwordEncoder.encode(userInfo.getPassword());
        userRepository.save(new UserInfo(
                UUID.randomUUID().toString(),
                userInfo.getUsername(),
                hashedPassword,
                userInfo.getEmail(),
                userInfo.getPhone(),
                0,
                new HashSet<>()
        ));
        return true;
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
