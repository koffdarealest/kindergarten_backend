package com.fsoft.fsa.kindergarten.config.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request -> request

                                .requestMatchers("/swagger-ui/**").anonymous()

                                .requestMatchers("/feedback/top/**").permitAll()

                                .requestMatchers("/auth/**").anonymous()

                                .requestMatchers("/auth/logout").authenticated()

                                .requestMatchers("/user/avatar").authenticated()

                                .requestMatchers("/school/currentSchool").hasAnyAuthority("PARENT")

                                .requestMatchers("/school/previousSchool").hasAnyAuthority("PARENT")

                                .requestMatchers("/school/utilities",
                                        "/school/age",
                                        "/school/facilities",
                                        "/school/type",
                                        "/school/filter/**",
                                        "/school/detail/**",
                                        "/school/rating/**",
                                        "/school/feedback/**",
                                        "/school/public/**").permitAll()

                                .requestMatchers("/user/**").hasAnyAuthority("ADMIN")

                                .requestMatchers("/school/**").hasAnyAuthority("ADMIN", "SCHOOL_OWNER")

                                .requestMatchers("/parent/info/**", "/request/countStatus", "/request/public", "/request/public/myRequest/**").hasAuthority("PARENT")

                                .requestMatchers("/request/**").hasAnyAuthority("ADMIN", "SCHOOL_OWNER")

                                .requestMatchers("/parent/**").hasAnyAuthority("ADMIN", "SCHOOL_OWNER")

                                .anyRequest().authenticated()
                )
                .exceptionHandling(
                        exceptionHandling -> exceptionHandling.accessDeniedHandler(customAccessDeniedHandler)
                                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider).addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }
}
