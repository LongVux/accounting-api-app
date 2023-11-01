package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.models.entity.BranchAccountEntryEntity;
import com.outwork.accountingapiapp.models.entity.ReceiptEntity;
import com.outwork.accountingapiapp.models.payload.requests.SaveReceiptEntryRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveReceiptRepaymentEntryRequest;
import com.outwork.accountingapiapp.repositories.BranchAccountEntryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
public class BranchAccountEntryService {

    @Autowired
    private BranchAccountEntryRepository branchAccountEntryRepository;

    @Autowired
    private ReceiptService receiptService;

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public ReceiptEntity confirmReceiptEntry (@Valid SaveReceiptEntryRequest request) {
        ReceiptEntity receipt = receiptService.approveReceiptForEntry(request.getReceiptId());

        branchAccountEntryRepository.saveAll(generateBranchAccountEntriesFromReceipt(receipt, request.getExplanation()));

        return receipt;
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public ReceiptEntity confirmRepayReceipt (@Valid SaveReceiptRepaymentEntryRequest request) {
        ReceiptEntity receipt = receiptService.repayReceiptForEntry(request);

        BranchAccountEntryEntity repaidEntry = new BranchAccountEntryEntity(
                receipt,
                request.getExplanation(),
                TransactionTypeEnum.REPAYMENT,
                request.getRepaidAmount()
        );

        branchAccountEntryRepository.save(repaidEntry);

        return receipt;
    }

    private List<BranchAccountEntryEntity> generateBranchAccountEntriesFromReceipt (ReceiptEntity receipt, String explanation) {
        Map<TransactionTypeEnum, Integer> receiptEntryMap = new HashMap<>();

        receiptEntryMap.put(TransactionTypeEnum.INTAKE, receipt.getIntake());
        receiptEntryMap.put(TransactionTypeEnum.PAYOUT, receipt.getPayout());
        receiptEntryMap.put(TransactionTypeEnum.LOAN, receipt.getLoan());
        receiptEntryMap.put(TransactionTypeEnum.REPAYMENT, receipt.getRepayment());

        return receiptEntryMap.keySet().stream()
                .filter(key -> ObjectUtils.isEmpty(receiptEntryMap.get(key)))
                .map(key -> new BranchAccountEntryEntity(
                        receipt,
                        explanation,
                        key,
                        receiptEntryMap.get(key)
                ))
                .toList();
    }

}
