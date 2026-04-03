package org.pavelleonov.spring.springboot.order_delivery_system_springboot.security;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.filters.NoCashFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.handlers.CustomAuthFailureHandler;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.handlers.CustomAuthSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomAuthFailureHandler customAuthFailureHandler;
    private final CustomAuthSuccessHandler customAuthSuccessHandler;
    private final CustomUserDetailService customUserDetailService;
    private final NoCashFilter noCashFilter;

    public SecurityConfig(CustomAuthFailureHandler customAuthFailureHandler,
                          CustomAuthSuccessHandler customAuthSuccessHandler,
                          CustomUserDetailService customUserDetailService,
                          NoCashFilter noCashFilter) {
        this.customAuthFailureHandler = customAuthFailureHandler;
        this.customAuthSuccessHandler = customAuthSuccessHandler;
        this.customUserDetailService = customUserDetailService;
        this.noCashFilter = noCashFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("api/auth/activate").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/me/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")        //
                        .successHandler(customAuthSuccessHandler)
                        .failureHandler(customAuthFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)   // уничтожаем сессию
                        .clearAuthentication(true)    // очищаем аутентификацию
                        .logoutSuccessUrl("/login?logout")
                )
                .addFilterAfter(noCashFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(customUserDetailService)
                .httpBasic(withDefaults());

        return http.build();
    }
}
