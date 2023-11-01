package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.models.entity.BillEntity;
import com.outwork.accountingapiapp.models.entity.PosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<BillEntity, UUID> {
    Optional<BillEntity> findFirstByCodeNotNullAndPosAndCreatedDateBetweenOrderByTimeStampOrderDesc(
            PosEntity pos,
            Date createdDateStart,
            Date createdDateEnd
    );
}
