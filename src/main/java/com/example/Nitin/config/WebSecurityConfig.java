package com.example.Nitin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .antMatchers("/h2-console/**", "/css/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                     //   .loginPage("/login") //Tell Spring to use your custom form
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout.permitAll())
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin")
                        .password("admin1234")
                        .roles("USER")
                        .build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();  // Use BCrypt in real apps!
    }


}
