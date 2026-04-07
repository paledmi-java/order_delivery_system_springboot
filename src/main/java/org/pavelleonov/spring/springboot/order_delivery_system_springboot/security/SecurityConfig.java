package org.pavelleonov.spring.springboot.order_delivery_system_springboot.security;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.filters.JwtFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.filters.NoCashFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.handlers.CustomAuthFailureHandler;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.handlers.CustomAuthSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomAuthFailureHandler customAuthFailureHandler;
    private final CustomAuthSuccessHandler customAuthSuccessHandler;
    private final CustomUserDetailService customUserDetailService;
    private final NoCashFilter noCashFilter;
    private final JwtFilter jwtFilter;

    public SecurityConfig(CustomAuthFailureHandler customAuthFailureHandler, CustomAuthSuccessHandler customAuthSuccessHandler, CustomUserDetailService customUserDetailService, NoCashFilter noCashFilter, JwtFilter jwtFilter) {
        this.customAuthFailureHandler = customAuthFailureHandler;
        this.customAuthSuccessHandler = customAuthSuccessHandler;
        this.customUserDetailService = customUserDetailService;
        this.noCashFilter = noCashFilter;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
     public AuthenticationProvider authenticationProvider(
             PasswordEncoder passwordEncoder
    ){
        DaoAuthenticationProvider daoAuthenticationProvider =
                new DaoAuthenticationProvider(customUserDetailService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/activate").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/me/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider(passwordEncoder()))

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)   // уничтожаем сессию
                        .clearAuthentication(true)    // очищаем аутентификацию
                        .logoutSuccessUrl("/login?logout")
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(noCashFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(customUserDetailService)
                .httpBasic(withDefaults());

        return http.build();
    }
}
