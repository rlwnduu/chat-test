package com.example.chat.global.security.user;

import com.example.chat.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    @Getter
    private final Long id;

    private final String username;

    private final String password;

    private final Set<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user, Set<? extends GrantedAuthority> authorities) {
        this.id = user.getId();
        this.username = user.getLoginId();
        this.password = user.getPassword();
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
