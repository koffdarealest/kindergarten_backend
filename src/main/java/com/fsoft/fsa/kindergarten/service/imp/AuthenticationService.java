package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.model.auth.AuthUser;
import com.fsoft.fsa.kindergarten.model.dto.auth.AuthenticationResponse;
import com.fsoft.fsa.kindergarten.model.form.auth.AuthenticationRequest;
import com.fsoft.fsa.kindergarten.repository.IUserRepository;
import com.fsoft.fsa.kindergarten.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final IUserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    @Value("${client.public.URL}")
    private String publicUrl;

    @Value("${client.private.URL}")
    private String privateUrl;

    public AuthenticationResponse authenticate(AuthenticationRequest request, String url) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        if (url.equals(publicUrl)) {
            if (user.getRole().getId() == 1 || user.getRole().getId() == 3) {
                throw new BadCredentialsException("Bad credentials");
            }
        } else if (url.equals(privateUrl)) {
            if (user.getRole().getId() == 2) {
                throw new BadCredentialsException("Bad credentials");
            }
        }
        if (!user.getStatus().equalsIgnoreCase("Active") || user.isDeleted()) {
            throw new BadCredentialsException("User is not active or deleted");
        }
        AuthUser authUser = new AuthUser(user);
        var jwtToken = jwtService.generateToken(authUser);
        var refreshToken = jwtService.generateRefreshToken();
        jwtTokenService.saveJwtToken(user, jwtToken, refreshToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
}
