package org.banking.service;

import org.banking.entities.UserInfo;
import org.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserInfo checkIfUserExists(UserInfo userInfo) {
        return userRepository.findByUsername(userInfo.getUsername());
    }

    public Boolean signUpUser(UserInfo userInfo) {
        if(Objects.nonNull(checkIfUserExists(userInfo))) {
            return false;
        }
        String userId = UUID.randomUUID().toString();
        userRepository.save(new UserInfo(userId,userInfo.getUsername(),userInfo.getPassword(),userInfo.getEmail(),userInfo.getPhone(),new HashSet<>()));
        return true;
    }
}
