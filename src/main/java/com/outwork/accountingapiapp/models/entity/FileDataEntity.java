package com.outwork.accountingapiapp.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "file_data")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDataEntity {

    @Id
    private String fileName;
    private String fileType;
}
