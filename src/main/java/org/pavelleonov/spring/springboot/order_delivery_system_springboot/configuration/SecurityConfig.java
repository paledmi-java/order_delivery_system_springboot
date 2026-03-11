package org.pavelleonov.spring.springboot.order_delivery_system_springboot.configuration;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.filters.NoCashFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.handlers.CustomAuthFailureHandler;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.handlers.CustomAuthSuccessHandler;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
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
    public UserDetailsManager userDetailsManager(DataSource dataSource){
        return  new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home","/login","/addClient").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/app/public/**").permitAll()
                        .requestMatchers("/app/admin/**").authenticated()
                        .requestMatchers("/app/user/**").authenticated()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
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
