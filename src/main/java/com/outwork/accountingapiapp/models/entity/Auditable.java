package com.outwork.accountingapiapp.models.entity;

import com.outwork.accountingapiapp.constants.RecordStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
public abstract class Auditable<U> {
    public static final String FIELD_CREATED_DATE = "createdDate";
    public static final String FIELD_CREATED_BY = "createdBy";
    public static final String FIELD_LAST_MODIFIED_BY = "lastModifiedBy";

    @CreatedBy
    protected U createdBy;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdDate;

    @LastModifiedBy
    protected U lastModifiedBy;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastModifiedDate;

    @Enumerated(EnumType.STRING)
    protected RecordStatusEnum recordStatusEnum;
}
