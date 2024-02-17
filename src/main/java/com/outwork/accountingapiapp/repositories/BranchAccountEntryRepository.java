package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.models.entity.BranchAccountEntryEntity;
import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.models.payload.responses.BranchAccountEntryTableItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface BranchAccountEntryRepository extends JpaRepository<BranchAccountEntryEntity, UUID> {
    Optional<BranchAccountEntryEntity> findFirstByEntryCodeNotNullAndBranchAndTransactionTypeAndCreatedDateBetweenOrderByCreatedDateDescTimeStampSeqDesc(BranchEntity branch, TransactionTypeEnum transactionType, Date createdDateStart, Date createdDateEnd);

    Page<BranchAccountEntryEntity> findAll (Specification<BranchAccountEntryEntity> specification, Pageable pageable);
}
