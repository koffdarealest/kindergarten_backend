package com.fsoft.fsa.kindergarten.service;

public interface TokenService {
    boolean isTokenValid(String token);

    void softDeleteToken(String token);

    void saveToken(String token, String email);
}
