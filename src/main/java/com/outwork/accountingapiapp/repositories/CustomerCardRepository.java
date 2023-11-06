package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.models.entity.CustomerCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerCardRepository extends JpaRepository<CustomerCardEntity, UUID> {
    boolean existsByAccountNumberAndBankIgnoreCaseAndIdNot(String accountNumber, String bank, UUID id);

    List<CustomerCardEntity> findByCustomer_Id(UUID id);
}
