package org.pavelleonov.spring.springboot.order_delivery_system_springboot.security;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Credentials;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailService implements UserDetailsService {

    private final ClientRepository clientRepository;

    public CustomUserDetailService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client client =
                clientRepository.findByCredentialsLogin(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(client);
    }
}
