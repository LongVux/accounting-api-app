package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.models.entity.UserBranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserBranchRepository extends JpaRepository<UserBranchEntity, UUID> {
}
