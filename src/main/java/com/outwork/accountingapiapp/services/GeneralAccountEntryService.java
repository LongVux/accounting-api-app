package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.constants.AccountEntryStatusEnum;
import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.BranchAccountEntryEntity;
import com.outwork.accountingapiapp.models.entity.GeneralAccountEntryEntity;
import com.outwork.accountingapiapp.models.payload.requests.*;
import com.outwork.accountingapiapp.models.payload.responses.AccountEntrySumUpInfo;
import com.outwork.accountingapiapp.models.payload.responses.GeneralAccountEntryTableItem;
import com.outwork.accountingapiapp.repositories.GeneralAccountEntryRepository;
import com.outwork.accountingapiapp.utils.DateTimeUtils;
import com.outwork.accountingapiapp.utils.GeneralAccountEntryCodeHandler;
import com.outwork.accountingapiapp.utils.Util;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
public class GeneralAccountEntryService {
    public static final String ERROR_MSG_INVALID_ACTION_ON_ENTRY = "Không thể thực hiện hành động này trên bút toán được chọn";

    @Autowired
    private GeneralAccountEntryRepository generalAccountEntryRepository;

    @Autowired
    private Util util;

    public GeneralAccountEntryEntity getEntryById (@NotNull UUID id) {
        return generalAccountEntryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public Page<GeneralAccountEntryTableItem> getBranchAccountEntryTableItems (GetGeneralAccountEntryTableItemRequest request) {
        return generalAccountEntryRepository.findAll(request, request.retrievePageConfig());
    }

    public AccountEntrySumUpInfo getGeneralAccountEntrySumUpInfo (GetGeneralAccountEntryTableItemRequest request) {
        Map<Object, Double> queryResult = util.getGroupedSumsBySpecification(request, BranchAccountEntryEntity.FIELD_TRANSACTION_TYPE, BranchAccountEntryEntity.FIELD_MONEY_AMOUNT, GeneralAccountEntryTableItem.class);

        AccountEntrySumUpInfo response = new AccountEntrySumUpInfo();

        response.setTotalIntake(queryResult.get(TransactionTypeEnum.INTAKE));
        response.setTotalIntake(queryResult.get(TransactionTypeEnum.PAYOUT));
        response.setTotalIntake(queryResult.get(TransactionTypeEnum.LOAN));
        response.setTotalIntake(queryResult.get(TransactionTypeEnum.REPAYMENT));
        response.setTotal(response.getTotalIntake() - response.getTotalPayout() - response.getTotalLoan() + response.getTotalRepayment());

        return response;
    }

    public GeneralAccountEntryEntity saveEntry (@Valid SaveGeneralAccountEntryRequest request, UUID id) {
        GeneralAccountEntryEntity savedEntry = ObjectUtils.isEmpty(id) ? new GeneralAccountEntryEntity() : getEntryById(id);

        savedEntry.setEntryType(request.getEntryType());
        savedEntry.setMoneyAmount(request.getMoneyAmount());
        savedEntry.setTransactionType(request.getTransactionType());
        savedEntry.setExplanation(request.getExplanation());
        savedEntry.setImageId(request.getImageId());
        savedEntry.setEntryStatus(AccountEntryStatusEnum.PENDING);

        validateAccountEntryForModification(savedEntry);

        return generalAccountEntryRepository.save(savedEntry);
    }

    public GeneralAccountEntryEntity approveEntry (@NotNull UUID id) {
        GeneralAccountEntryEntity approvedEntry = getEntryById(id);

        validateAccountEntryForModification(approvedEntry);

        approvedEntry.setEntryCode(getNewGeneralEntryCode(approvedEntry));
        approvedEntry.setEntryStatus(AccountEntryStatusEnum.APPROVED);
        approvedEntry.setCreatedDate(new Date());

        return generalAccountEntryRepository.save(approvedEntry);
    }

    public void deleteBranchAccountEntry (@NotNull UUID id) {
        GeneralAccountEntryEntity entry = getEntryById(id);

        validateAccountEntryForModification(entry);

        generalAccountEntryRepository.deleteById(id);
    }

    private void validateAccountEntryForModification(GeneralAccountEntryEntity entry) {
        if (!ObjectUtils.isEmpty(entry.getEntryCode())) {
            throw new InvalidDataException(ERROR_MSG_INVALID_ACTION_ON_ENTRY);
        }
    }

    private String getNewGeneralEntryCode (GeneralAccountEntryEntity entry) {
        Optional<GeneralAccountEntryEntity> latestEntry = generalAccountEntryRepository.findFirstByEntryCodeNotNullAndTransactionTypeAndCreatedDateBetweenOrderByCreatedDateDesc(
                entry.getTransactionType(),
                DateTimeUtils.atStartOfDay(new Date()),
                DateTimeUtils.atEndOfDay(new Date())
        );

        return GeneralAccountEntryCodeHandler.generateAccountEntryCode(
                entry.getTransactionType(),
                latestEntry.map(GeneralAccountEntryEntity::getEntryCode).orElse(null)
        );
    }
}
