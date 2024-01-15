package com.outwork.accountingapiapp.models.payload.responses;

import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.models.entity.AccountEntryTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountEntryTypeRepository extends JpaRepository<AccountEntryTypeEntity, UUID> {
    List<AccountEntryTypeEntity> findByTitleContainsIgnoreCase(String title);

    List<AccountEntryTypeEntity> findByTitleContainsIgnoreCaseAndTransactionType(String title,
                                                                                 TransactionTypeEnum transactionType);
}
