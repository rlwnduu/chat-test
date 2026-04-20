package com.example.chat.user.repository;

import com.example.chat.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByUsername(String username);

    List<User> findByIdIn(Collection<String> ids);
}
