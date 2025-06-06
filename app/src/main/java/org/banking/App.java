/*
 * This source file was generated by the Gradle 'init' task
 */
package org.banking;

import org.banking.entities.UserRole;
import org.banking.repository.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

//    @Bean
//    CommandLineRunner initRoles(UserRoleRepository userRoleRepository) {
//        return args -> {
//            if (userRoleRepository.findByName("USER").isEmpty()) {
//                userRoleRepository.save(new UserRole("USER"));
//                System.out.println("USER role initialized.");
//            }
//            if (userRoleRepository.findByName("ADMIN").isEmpty()) {
//                userRoleRepository.save(new UserRole("ADMIN"));
//                System.out.println("ADMIN role initialized.");
//            }
//        };
//    }
}
