package org.banking.controller;

import org.banking.entities.UserInfo;
import org.banking.request.LoginRequestDTO;
import org.banking.response.JwtResponseDTO;
import org.banking.service.AuthService;
import org.banking.service.JwtService;
import org.banking.service.BankUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthService authService;


    @PostMapping("auth/v1/signup")
    public ResponseEntity<?> signup(@RequestBody UserInfo userInfo) {
        try {
            String userId = String.valueOf(authService.signUpUser(userInfo));
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
            }

            String jwtToken = jwtService.generateToken(userInfo.getUsername());
            JwtResponseDTO response = JwtResponseDTO.builder()
                    .accessToken(jwtToken)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception occurred in User Service: " + ex.getMessage());
        }
    }

    @PostMapping("auth/v1/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Boolean chk = authService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
            if (!chk) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials");
            }
            String jwtToken = jwtService.generateToken(loginRequest.getUsername());
            return ResponseEntity.ok(jwtToken);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception occurred in User Service: " + ex.getMessage());
        }
    }
}
