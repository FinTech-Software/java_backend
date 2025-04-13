package org.banking.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="transactions")
public class UserTransaction {

    @Id
    @Column(name="transaction_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id")
    private UserInfo sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "user_id")
    private UserInfo receiver;

    private int amount;

    private LocalDateTime date;

    private Boolean status;
}
