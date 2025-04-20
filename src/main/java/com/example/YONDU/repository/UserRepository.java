package com.example.YONDU.repository;

import com.example.YONDU.entity.UserEntity;
import com.example.YONDU.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByIdentifier(String identifier);
    boolean existsByIdentifier(String identifier);

    Optional<UserEntity> findByPhone(String Phone);
    boolean existsByPhone(String Phone);

    Optional<UserEntity> findByRefreshToken(String refreshToken);

    List<UserEntity> findByRole(Role role);

}
