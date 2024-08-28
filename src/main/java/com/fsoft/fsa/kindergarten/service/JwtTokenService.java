package com.fsoft.fsa.kindergarten.service;

import com.fsoft.fsa.kindergarten.model.dto.auth.AuthenticationResponse;
import com.fsoft.fsa.kindergarten.model.entity.User;

public interface JwtTokenService {

    void saveJwtToken(User user, String accessToken, String refreshToken);

    AuthenticationResponse getNewToken(String refreshToken);

    void logout(String refreshToken);
}