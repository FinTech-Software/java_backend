package org.banking.repository;

import org.banking.entities.UserInfo;
import org.banking.entities.UserTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTransactionRepository extends JpaRepository<UserTransaction, String> {
    List<UserTransaction> findBySenderOrReceiver(UserInfo sender, UserInfo receiver);
}
