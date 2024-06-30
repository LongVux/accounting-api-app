package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.models.entity.BillEntity;
import com.outwork.accountingapiapp.models.entity.PosEntity;
import com.outwork.accountingapiapp.models.payload.responses.BillTableItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<BillEntity, UUID> {
    Optional<BillEntity> findFirstByCodeNotNullAndPosAndCreatedDateBetweenOrderByConfirmedDateDescTimeStampSeqDesc(
            PosEntity pos,
            Date createdDateStart,
            Date createdDateEnd
    );

    Page<BillEntity> findAll(Specification<BillEntity> specification, Pageable pageable);

    List<BillEntity> findByPos_IdAndCreatedDateBetweenAndCodeNotNullAndReturnedTimeNullOrderByCreatedDateAscTimeStampSeqAsc(UUID id, Date createdDateStart,
                                                                            Date createdDateEnd);

    List<BillEntity> findByPos_IdAndCreatedDateBetweenOrderByCreatedDateAscTimeStampSeqAsc(UUID posId, Date fromCreatedDate, Date toCreatedDate);
}
