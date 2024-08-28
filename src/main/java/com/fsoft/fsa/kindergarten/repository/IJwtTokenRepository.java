package com.fsoft.fsa.kindergarten.repository;

import com.fsoft.fsa.kindergarten.model.entity.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface IJwtTokenRepository extends JpaRepository<JwtToken, Integer> ,JpaSpecificationExecutor<JwtToken> {
    Optional<JwtToken> findByRefreshToken(String refreshToken);
}
