package ru.nikita.CRUD.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.nikita.CRUD.config.security.LibraryUserDetailService;

@Configuration
public class Security {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/main-page").hasAnyRole("ADMIN","USER")
                                .requestMatchers("/process_login").permitAll()
                                .requestMatchers("/login", "/resources/**").permitAll()
                                .requestMatchers("/process_login").permitAll()
                                .requestMatchers("/registration").permitAll()
                                .requestMatchers("/new-user").permitAll()
                                .requestMatchers("book/new").hasAnyRole("ADMIN","USER")
                                .requestMatchers("/book/deleteAllBooksByTitle").hasRole("ADMIN")
                                .requestMatchers("/book/deleteGroup/").hasRole("ADMIN")
                                .requestMatchers("/people/new").permitAll()
                                .requestMatchers("/book/{id}/newOwner").hasRole("ADMIN")
                                .requestMatchers("/people/{id}/edit").hasRole("ADMIN")
                                .requestMatchers("/people/{id}").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("/people/showAll").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/book/showall").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/book/{id}").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST).hasRole("ADMIN")
                                .requestMatchers("/login-page").permitAll()
                                .requestMatchers("/book/{id}/deleteOwner").hasAnyRole("ADMIN", "USER")
                                .anyRequest().permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/Error/AccessError"))
                .formLogin(f ->
                        f.loginProcessingUrl("/process_login")
                                .loginPage("/login")
                                .defaultSuccessUrl("/main-page",true)
                                .failureForwardUrl("/login-page?error=true")
                                .permitAll())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
