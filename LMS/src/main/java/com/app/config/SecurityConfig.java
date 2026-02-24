package com.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.app.security.CustomSuccessHandler;
import com.app.security.CustomUserDetailsService;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomSuccessHandler customSuccessHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService, CustomSuccessHandler customSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customSuccessHandler = customSuccessHandler;
    }
    
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    
    
    @Bean
    public org.springframework.security.authentication.dao.DaoAuthenticationProvider authenticationProvider() {
        var authProvider = new org.springframework.security.authentication.dao.DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home/**", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/instructor/login", "/instructor/register").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/instructor/**").hasRole("INSTRUCTOR")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/instructor/login")   // your existing login page
                // Use a distinct processing URL so controller's POST mapping doesn't conflict
                .loginProcessingUrl("/perform_login")
                .usernameParameter("email")
                .successHandler(customSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
            	    .logoutUrl("/logout")
            	    .logoutSuccessUrl("/instructor/login?logout")
            	    .invalidateHttpSession(true)
            	    .clearAuthentication(true)
            	    .deleteCookies("JSESSIONID")
            	    .permitAll()
            	);

        return http.build();
    }
}