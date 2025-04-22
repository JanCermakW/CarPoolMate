package com.carpoolmate.carpoolmate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // Admin-only area
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Public resources and pages
                .requestMatchers(
                        "/registration/**",
                        "/registration/verify**",
                        "/forgotPasswd",
                        "/forgotPasswd/verify**",
                        "/js/**",
                        "/styles/css/**",
                        "/img/**",
                        "/",
                        "/home",
                        "/about",
                        "/swagger-ui.html"
                ).permitAll()

                // Endpoints that require authenticated users (with USER or higher role)
                .requestMatchers("/user/**", "/api/user/**", "/rides/book/**", "/profiles/**").hasRole("USER")
                .requestMatchers( "/rides/create/**", "/api/user/**").hasRole("DRIVER")

                // Everything else is accessible (optional: you could also require authentication by default)
                .anyRequest().permitAll()

                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()

                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll();

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        // Definice hierarchie: ADMIN > DRIVER > USER
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_DRIVER \n ROLE_DRIVER > ROLE_USER");
        return roleHierarchy;
    }

}

