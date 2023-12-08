package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.models.entity.GeneralAccountEntryEntity;
import com.outwork.accountingapiapp.models.payload.responses.GeneralAccountEntryTableItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface GeneralAccountEntryRepository extends JpaRepository<GeneralAccountEntryEntity, UUID> {
    Page<GeneralAccountEntryEntity> findAll (Specification<GeneralAccountEntryEntity> specification, Pageable pageable);

    Optional<GeneralAccountEntryEntity> findFirstByEntryCodeNotNullAndTransactionTypeAndLastModifiedDateBetweenOrderByLastModifiedDateDesc(TransactionTypeEnum transactionType, Date lastModifiedDateStart, Date lastModifiedDateEnd);
}
