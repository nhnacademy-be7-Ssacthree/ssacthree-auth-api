package com.nhnacademy.ssacthree_auth_api.repository;

import com.nhnacademy.ssacthree_auth_api.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    boolean existsByRefreshToken(String refreshToken);


    void deleteByRefreshToken(String refreshToken);


}
