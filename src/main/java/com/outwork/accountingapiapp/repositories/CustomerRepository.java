package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.models.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    boolean existsByNationalIdAndIdNot(String nationalId, UUID id);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, UUID id);
}
