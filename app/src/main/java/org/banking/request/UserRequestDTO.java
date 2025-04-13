package org.banking.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {
    private String id;
    private String username;
    private String email;
    private String phone;
    private Set<String> roles;
}
