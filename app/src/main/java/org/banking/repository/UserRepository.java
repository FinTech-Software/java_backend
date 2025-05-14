package org.banking.repository;

import org.banking.entities.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserInfo,String> {
    Optional<UserInfo> findByUsername(String username);

    List<UserInfo> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);

}
