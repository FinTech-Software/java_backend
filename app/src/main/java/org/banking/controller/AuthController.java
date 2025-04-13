package org.banking.controller;

import org.banking.entities.UserInfo;
import org.banking.request.LoginRequestDTO;
import org.banking.response.JwtResponseDTO;
import org.banking.service.AuthService;
import org.banking.service.JwtService;
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


    @PostMapping("v1/auth/signup")
    public ResponseEntity<?> signup(@RequestBody UserInfo userInfo) {
        try {
            //Fetching the user Id from the service to check if user exists or not
            String userId = String.valueOf(authService.signUpUser(userInfo));
            if (userId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
            }
            //Generating a jwt token and sending it in accessToken
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

    @PostMapping("v1/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            //Sending username and password to service
            boolean loginUser = authService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
            if (!loginUser) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials");
            }
            //Generating a jwt token and sending it to the client
            String jwtToken = jwtService.generateToken(loginRequest.getUsername());
            return ResponseEntity.ok(jwtToken);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception occurred in User Service: " + ex.getMessage());
        }
    }
}
