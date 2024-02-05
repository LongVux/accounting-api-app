package com.outwork.accountingapiapp.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.outwork.accountingapiapp.constants.DataConstraint;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Table(name = "customer_cards",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"accountNumber", "bank"})}
)
public class CustomerCardEntity extends Auditable<String> {
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_CARD_TYPE = "cardType";
    public static final String FIELD_BANK = "bank";
    public static final String FIELD_NATIONAL_ID = "nationalId";
    public static final String FIELD_PAYMENT_LIMIT = "paymentLimit";
    public static final String FIELD_PAYMENT_DUE_DATE = "paymentDueDate";
    public static final String FIELD_CUSTOMER = "customer";
    public static final String FIELD_ACCOUNT_NUMBER = "accountNumber";
    public static final String FIELD_EXPIRED_DATE = "expiredDate";

    public static final String ERROR_MSG_PRE_PAID_FEE_NOT_ENOUGH = "Số tiền ứng trước của thẻ không đủ để thực hiện hành động này";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String name;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "cardTypeId", nullable = false)
    private CardTypeEntity cardType;

    @Column(nullable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String accountNumber;

    @Column(nullable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String bank;

    private int paymentLimit;

    private int paymentDueDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredDate;

    private String note;

    @Setter(AccessLevel.NONE)
    private double prePaidFee;

    private String prePaidFeeReceiverCode;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "customerId", nullable = false)
    private CustomerEntity customer;

    @JsonIgnore
    @OneToMany(mappedBy = "customerCard", fetch = FetchType.LAZY)
    private List<ReceiptEntity> receipts;

    public void setPrePaidFee (double prePaidFee) {
        if (prePaidFee < 0) {
            throw new InvalidDataException(ERROR_MSG_PRE_PAID_FEE_NOT_ENOUGH);
        } else {
            this.prePaidFee = prePaidFee;
        }
    }
}
