package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.config.exception.ResourceNotFoundException;
import com.fsoft.fsa.kindergarten.model.auth.AuthUser;
import com.fsoft.fsa.kindergarten.model.dto.auth.AuthenticationResponse;
import com.fsoft.fsa.kindergarten.model.entity.JwtToken;
import com.fsoft.fsa.kindergarten.model.entity.User;
import com.fsoft.fsa.kindergarten.repository.specification.JwtTokenSpecification;
import com.fsoft.fsa.kindergarten.repository.IJwtTokenRepository;
import com.fsoft.fsa.kindergarten.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtTokenServiceImp implements JwtTokenService {

    private final IJwtTokenRepository jwtTokenRepository;
    private final JwtService jwtService;

    @Value("${jwt.access-token.expiration}")
    private long ACCESS_TOKEN_EXPIRATION;

    @Value("${jwt.refresh-token.expiration}")
    private long REFRESH_TOKEN_EXPIRATION;

    public void saveJwtToken(User user, String accessToken, String refreshToken) {
        Optional<JwtToken> tk = findByUserId(user.getId());
        if (tk.isPresent()) {
            JwtToken jwtToken = tk.get();
            jwtToken.setAccessToken(accessToken)
                    .setAccessTokenExpiryTime(new Date(System.currentTimeMillis() + (ACCESS_TOKEN_EXPIRATION)))
                    .setRefreshToken(refreshToken)
                    .setRefreshTokenExpiryTime(new Date(System.currentTimeMillis() + (REFRESH_TOKEN_EXPIRATION)));
            jwtTokenRepository.save(jwtToken);
            return;
        }
        JwtToken jwtToken = JwtToken.builder()
                .user(user)
                .accessToken(accessToken)
                .accessTokenExpiryTime(new Date(System.currentTimeMillis() + (ACCESS_TOKEN_EXPIRATION)))
                .refreshToken(refreshToken)
                .refreshTokenExpiryTime(new Date(System.currentTimeMillis() + (REFRESH_TOKEN_EXPIRATION)))
                .isRevoked(false)
                .build();
        jwtTokenRepository.save(jwtToken);
    }

    private Optional<JwtToken> findByRefreshToken(String refreshToken) {
        return jwtTokenRepository.findOne(JwtTokenSpecification.hasRefreshToken(refreshToken));
    }

    private Optional<JwtToken> findByUserId(Integer userId) {
        return jwtTokenRepository.findOne(JwtTokenSpecification.hasUserId(userId));
    }

    public void deleteToken(String refreshToken) {
        Optional<JwtToken> jwtToken = jwtTokenRepository.findByRefreshToken(refreshToken);
        jwtToken.ifPresentOrElse(jwtTokenRepository::delete,
                () -> { throw new ResourceNotFoundException("Refresh token not found");});
    }

    public void logout(String refreshToken) {
        deleteToken(refreshToken);
    }

    public AuthenticationResponse getNewToken(String refreshToken) {
        var jwtToken = findByRefreshToken(refreshToken).orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
        if (jwtToken.getIsRevoked()) {
            throw new RuntimeException("Refresh token is revoked");
        }
        if (jwtToken.getRefreshTokenExpiryTime().before(new Date())) {
            throw new RuntimeException("Refresh token is expired");
        }
        var user = jwtToken.getUser();
        AuthUser authUser = new AuthUser(user);
        var newJwtToken = jwtService.generateToken(authUser);
        var newRefreshToken = jwtService.generateRefreshToken();
        jwtToken.setAccessToken(newJwtToken);
        jwtToken.setRefreshToken(newRefreshToken);
        jwtTokenRepository.save(jwtToken);
        return new AuthenticationResponse(newJwtToken, newRefreshToken);
    }

}
