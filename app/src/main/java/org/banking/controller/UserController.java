package org.banking.controller;

import org.banking.entities.UserInfo;
import org.banking.request.UserRequestDTO;
import org.banking.response.UserDetailsResponseDTO;
import org.banking.service.BankUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private BankUserDetailsService bankUserDetailsService;

    @PostMapping("v1/user/getUserDetails")
    public ResponseEntity<UserDetailsResponseDTO> getUserDetails(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            String username = userRequestDTO.getUsername();
            UserInfo user = bankUserDetailsService.getUserDetailsByUsername(username);

            UserDetailsResponseDTO responseDTO = new UserDetailsResponseDTO();
            responseDTO.setId(user.getId());
            responseDTO.setUsername(user.getUsername());
            responseDTO.setEmail(user.getEmail());
            responseDTO.setBalance(user.getAccountBalance());

            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserDetailsResponseDTO());
        }
    }
    @GetMapping("v1/user/search")
    public ResponseEntity<List<UserDetailsResponseDTO>> searchUser(@RequestParam("query") String query) {
        try{
            List<UserInfo> matchedUsers = bankUserDetailsService.searchUsersByQuery(query);

            List<UserDetailsResponseDTO> response = matchedUsers.stream().map(user -> {
                UserDetailsResponseDTO dto = new UserDetailsResponseDTO();
                dto.setId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setEmail(user.getEmail());
                dto.setBalance(user.getAccountBalance());
                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
