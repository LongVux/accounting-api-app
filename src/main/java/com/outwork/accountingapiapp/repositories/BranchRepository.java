package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.models.payload.responses.SuggestedBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, UUID> {

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, UUID id);
    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    List<BranchEntity> findByIdIn(Collection<UUID> ids);

    List<SuggestedBranch> findByCodeContainsIgnoreCase(String code);

    Optional<BranchEntity> findByCode(String code);
}
