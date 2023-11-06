package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.models.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByCode(String code);
    Optional<UserEntity> findByCode(String code);

    Page<UserEntity> findAll (Specification<UserEntity> userEntitySpecification, Pageable pageable);
}
