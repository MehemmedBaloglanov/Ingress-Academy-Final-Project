package com.lastversion.user.repository;

import com.lastversion.user.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(@NotBlank String email);

    Optional<UserEntity> findByUserId(UUID id);
}
