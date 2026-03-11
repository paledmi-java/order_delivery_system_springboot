package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Credentials;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.CredentialsRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailService implements UserDetailsService {

    private final CredentialsRepository clientRepository;

    public CustomUserDetailService(CredentialsRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Credentials credentials =
                clientRepository.findByLogin(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(credentials.getLogin())
                .password(credentials.getHashedPassword())
                .roles("USER")
                .build();
    }
}
