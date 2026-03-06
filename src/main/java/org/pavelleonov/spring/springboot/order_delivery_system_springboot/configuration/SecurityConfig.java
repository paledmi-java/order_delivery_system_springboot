package org.pavelleonov.spring.springboot.order_delivery_system_springboot.configuration;

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
                        .requestMatchers("/", "/home","/login").permitAll()           // корневой URL открыт
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/app/public/**").permitAll()      // открытые фронт страницы
                        .requestMatchers("/app/admin/**").authenticated()  // защищённый фронт
                        .requestMatchers("/api/public/**").permitAll()      // открытый REST
                        .requestMatchers("/api/**").authenticated()        // REST требует логин
                )
                .formLogin(form -> form
                        .loginPage("/login")        // кастомная форма логина
                        .defaultSuccessUrl("/app/admin")
                        .permitAll()
                )
                .httpBasic(withDefaults());

        return http.build();
    }
}
