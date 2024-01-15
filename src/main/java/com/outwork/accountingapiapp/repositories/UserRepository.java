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
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, UUID id);
    boolean existsByEmailAndIdNot(String email, UUID id);
    boolean existsByCodeAndIdNot(String code, UUID id);
    Optional<UserEntity> findByCode(String code);

    Page<UserEntity> findAll (Specification<UserEntity> userEntitySpecification, Pageable pageable);

    boolean existsByAccountNumberAndBankIgnoreCaseAndIdNot(String accountNumber, String bank, UUID id);
}
