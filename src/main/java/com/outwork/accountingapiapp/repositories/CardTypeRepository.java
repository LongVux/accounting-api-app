package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.models.entity.CardTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CardTypeRepository extends JpaRepository<CardTypeEntity, UUID> {
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
}
