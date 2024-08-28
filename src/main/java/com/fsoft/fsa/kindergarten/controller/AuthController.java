package com.fsoft.fsa.kindergarten.controller;

import com.fsoft.fsa.kindergarten.config.Translator;
import com.fsoft.fsa.kindergarten.model.dto.auth.AuthenticationResponse;
import com.fsoft.fsa.kindergarten.model.form.auth.AuthenticationRequest;
import com.fsoft.fsa.kindergarten.model.form.user.ForgotEmailForm;
import com.fsoft.fsa.kindergarten.model.form.user.RegisterForm;
import com.fsoft.fsa.kindergarten.model.form.user.ResetPasswordForm;
import com.fsoft.fsa.kindergarten.service.JwtTokenService;
import com.fsoft.fsa.kindergarten.service.UserService;
import com.fsoft.fsa.kindergarten.service.imp.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Authentication Controller")
@Validated
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final JwtTokenService jwtTokenService;


    @PostMapping("/login")
    public AuthenticationResponse authenticate(
           @Valid @RequestBody AuthenticationRequest request,
           HttpServletRequest httpServletRequest
    ) {
        String url = httpServletRequest.getHeader("Url");
        System.out.println("Url: " + url);
        return authenticationService.authenticate(request, url);
    }

    @Operation(summary = "Get forgot email", description = "Get email of forgot account")
    @PostMapping("/forgot")
    public String getForgotEmail(@Valid @RequestBody ForgotEmailForm email) throws MessagingException, IOException {
        log.info("Request forgot email, email = " + email);
        userService.forgotPassword(email.getEmail());
        return Translator.toLocale("user.forgot.email.success");
    }

    @Operation(summary = "Get reset token", description = "Get token before reset")
    @GetMapping("/reset/{token}")
    public String resetPassword(@PathVariable String token) {
        log.info("Get token to validate, token = " + token);
        userService.isValidToken(token);
        return Translator.toLocale("user.forgot.token.valid");

    }

    @Operation(summary = "Reset password", description = "Reset password")
    @PutMapping("/reset/{token}")
    public String resetPassword(@PathVariable String token, @Valid @RequestBody ResetPasswordForm resetPasswordForm) {
        log.info("Request reset password, token = " + token);
        userService.resetPassword(token, resetPasswordForm);
        return Translator.toLocale("user.reset.success");
    }

    @Operation(summary = "Logout", description = "Logout")
    @PostMapping("/logout")
    public String logout(@RequestBody String refreshToken) {
        log.info("Request logout");
        jwtTokenService.logout(refreshToken);
        return Translator.toLocale("user.logout.success");
    }

    @Operation(summary = "Refresh token", description = "Refresh token")
    @PostMapping("/refreshToken")
    public AuthenticationResponse refreshToken(@RequestBody String refreshToken) {
        return jwtTokenService.getNewToken(refreshToken);
    }

    @Operation(summary = "Register", description = "Register")
    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterForm registerForm) {
        log.info("Request register");
        userService.register(registerForm);
        return Translator.toLocale("user.add.success");
    }

}
