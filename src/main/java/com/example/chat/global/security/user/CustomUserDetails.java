package com.example.chat.global.security.user;

import com.example.chat.user.domain.User;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@ToString(exclude = "password")
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Set<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getLoginId();
        this.password = user.getPassword();
        this.authorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getRole().getKey()))
                .collect(Collectors.toSet());
    }
}
