package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.models.payload.responses.SuggestedBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, UUID> {
    boolean existsByAccountNumberAndBankIgnoreCaseAndIdNot(String accountNumber, String bank, UUID id);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, UUID id);
    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
    boolean existsByPhoneNumberIgnoreCase(String phoneNumber);
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByNameIgnoreCase(String name);
    List<BranchEntity> findByIdIn(Collection<UUID> ids);

    List<SuggestedBranch> findByCodeContainsIgnoreCase(String code);
}
