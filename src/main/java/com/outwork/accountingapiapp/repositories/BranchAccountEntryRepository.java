package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.models.entity.BranchAccountEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BranchAccountEntryRepository extends JpaRepository<BranchAccountEntryEntity, UUID> {
}
