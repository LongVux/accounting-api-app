package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.models.entity.BranchAccountEntryEntity;
import com.outwork.accountingapiapp.models.entity.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface BranchAccountEntryRepository extends JpaRepository<BranchAccountEntryEntity, UUID> {
    Optional<BranchAccountEntryEntity> findFirstByEntryCodeNotNullAndBranchAndTransactionTypeAndCreatedDateBetweenOrderByCreatedDateDesc(BranchEntity branch, TransactionTypeEnum transactionType, Date createdDateStart, Date createdDateEnd);
}
