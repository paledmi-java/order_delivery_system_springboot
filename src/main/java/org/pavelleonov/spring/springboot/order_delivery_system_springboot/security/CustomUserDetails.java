package org.pavelleonov.spring.springboot.order_delivery_system_springboot.security;

import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    @Getter
    private final Integer id;
    private final String username;
    private final String password;
    private final List <GrantedAuthority> authorities;

    public CustomUserDetails(Client client) {
        this.id = client.getClientId();
        this.username = client.getCredentials().getLogin();
        this.password = client.getCredentials().getHashedPassword();
        this.authorities = client.getRoles().stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role.getName()))
                .toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
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
