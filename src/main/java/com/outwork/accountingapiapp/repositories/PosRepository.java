package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.constants.PosStatusEnum;
import com.outwork.accountingapiapp.models.entity.PosEntity;
import com.outwork.accountingapiapp.models.payload.responses.SuggestedPos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface PosRepository extends JpaRepository<PosEntity, UUID> {
    List<SuggestedPos> findByCodeContainsIgnoreCaseAndPosStatus(@NonNull String code, PosStatusEnum posStatus);
    List<PosEntity> findByIdInAndSupportedCardTypes_CardType_Id(Collection<UUID> ids, UUID id);
    boolean existsByAccountNumberAndBankIgnoreCaseAndIdNot(String accountNumber, String bank, UUID id);
    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);
}
