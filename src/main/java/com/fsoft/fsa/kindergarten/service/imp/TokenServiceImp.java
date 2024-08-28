package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.model.entity.Token;
import com.fsoft.fsa.kindergarten.repository.specification.TokenSpecification;
import com.fsoft.fsa.kindergarten.repository.ITokenRepository;
import com.fsoft.fsa.kindergarten.repository.IUserRepository;
import com.fsoft.fsa.kindergarten.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenServiceImp implements TokenService {

    private final ITokenRepository tokenRepository;
    private final IUserRepository userRepository;

    @Override
    public boolean isTokenValid(String token) {
        return tokenRepository.exists(TokenSpecification.hasToken(token)
                .and(TokenSpecification.isNotExpired()
                .and(TokenSpecification.isNotUsed())));
    }

    @Override
    public void softDeleteToken(String token) {
        Optional<Token> tk = tokenRepository.findOne(TokenSpecification.hasToken(token));
        if (tk.isPresent()) {
            tk.get().setIsUsed(true);
            tokenRepository.save(tk.get());
        }
    }

    @Override
    public void saveToken(String token, String email) {
        tokenRepository.save(Token.builder()
                .token(token)
                .expiryTime(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .isUsed(false)
                .user(userRepository.findByEmail(email).orElseThrow())
                .build());
    }


}
