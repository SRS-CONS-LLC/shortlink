package com.srscons.shortlink.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Accessors(chain = true)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String name;
    private String surname;
    private String fullName;
    private String phone;
    private String providerId;
    private String sub;
    private String picture;
    private int status;
    private int balance;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TokenEntity> tokens;

    @Override
    public String toString() {
        return "id=" + id +
                ", email=" + email +
                ", name=" + name +
                ", surname=" + surname +
                ", fullname=" + fullName +
                ", providerId=" + providerId +
                ", status=" + status +
                ", balance=" + balance +
                ", subId=" + sub
                ;
    }
}
