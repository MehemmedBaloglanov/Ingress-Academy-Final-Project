package com.lastversion.user.repository;

import com.lastversion.common.entity.AdminEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<AdminEntity, UUID> {
    Optional<AdminEntity> findByEmail(@NotBlank String email);
}
