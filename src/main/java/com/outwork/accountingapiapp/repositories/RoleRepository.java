package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.models.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    boolean existsByTitleIgnoreCase(String title);
    List<RoleEntity> findByIdIn(Collection<UUID> ids);
}
