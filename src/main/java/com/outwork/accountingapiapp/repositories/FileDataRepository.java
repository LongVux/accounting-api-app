package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.models.entity.FileDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDataRepository extends JpaRepository<FileDataEntity, String> {
}
