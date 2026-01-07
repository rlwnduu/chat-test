package com.example.chat.user.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "authorities")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(nullable = false, length = 50)
    private String username;

    @Getter
    @Column(nullable = false, length = 50)
    private String authority;
}