package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.BillEntity;
import com.outwork.accountingapiapp.models.entity.PosCardFeeEntity;
import com.outwork.accountingapiapp.models.entity.PosEntity;
import com.outwork.accountingapiapp.models.entity.ReceiptEntity;
import com.outwork.accountingapiapp.models.payload.requests.GetBillTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.GetMatchingBillsRequest;
import com.outwork.accountingapiapp.models.payload.requests.MatchingBillRequest;
import com.outwork.accountingapiapp.models.payload.requests.ReceiptBill;
import com.outwork.accountingapiapp.models.payload.responses.BillSumUpInfo;
import com.outwork.accountingapiapp.models.payload.responses.BillTableItem;
import com.outwork.accountingapiapp.repositories.BillRepository;
import com.outwork.accountingapiapp.utils.BillCodeHandler;
import com.outwork.accountingapiapp.utils.DateTimeUtils;
import com.outwork.accountingapiapp.utils.Util;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BillService {
    public static final String ERROR_MSG_NO_POS_FEE_FOUND_FOR_THE_BILL = "Không tìm thấy phí POS cho loại thẻ của bill";
    public static final String ERROR_MSG_BILL_ALREADY_HAS_CODE = "Bill đã được tạo mã. Không thể xử lý";
    public static final String ERROR_MSG_SOME_BILL_INVALID_TO_APPROVE = "Một số bill không hợp lệ để tạo bút toán";
    public static final String ERROR_MSG_NO_BILL_TO_APPROVE = "Không có bill để tạo bút toán";
    public static final String ERROR_MSG_BILL_VALUE_EXCEED_POS_LIMIT = "Giá trị Bill vượt quá giới hạn của POS";
    public static final String ERROR_MSG_SOME_BILL_IDS_NOT_FOUND = "Một số bill không tồn tại";
    public static final String ERROR_MSG_MONEY_AMOUNT_DOES_NOT_MATCH_BILL = "Số tiền không khớp với bills";

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PosService posService;

    @Autowired
    private Util util;

    public BillEntity getBillById (@NotNull UUID id) {
        return billRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public Page<BillTableItem> getBillTableItems (GetBillTableItemRequest request) {
        return billRepository.findAll(request, request.retrievePageConfig()).map(BillTableItem::new);
    }

    public BillSumUpInfo getBillSumUpInfo (GetBillTableItemRequest request) {
        List<Double> result = util.getSumsBySpecifications(Collections.singletonList(request), BillEntity.getSumUpFields(), BillEntity.class);

        BillSumUpInfo sumUpInfo = new BillSumUpInfo();

        sumUpInfo.setTotalMoneyAmount(result.get(0));
        sumUpInfo.setTotalEstimatedProfit(result.get(1));
        sumUpInfo.setTotalReturnedProfit(result.get(2));

        return sumUpInfo;
    }

    public void estimateBillProfit(BillEntity bill) {
        int posFee = getPosFeeFromBillByCardTypeId(bill);
        bill.setEstimatedProfit(bill.getFee() - bill.getMoneyAmount()*posFee/100);
    }

    public int getPosFeeFromBillByCardTypeId (@NotNull BillEntity bill) {
        for (PosCardFeeEntity cardFee : bill.getPos().getSupportedCardTypes()) {
            if (cardFee.getCardType().getId().equals(bill.getReceipt().getCustomerCard().getCardType().getId())) {
                return cardFee.getPosCardFee();
            }
        }

        throw new EntityNotFoundException(ERROR_MSG_NO_POS_FEE_FOUND_FOR_THE_BILL);
    }

    public void buildBillsForReceipt (List<ReceiptBill> billRequests, @NotNull ReceiptEntity savedReceipt) {
        if (CollectionUtils.isEmpty(billRequests)) {
            return;
        }

        Map<UUID, PosEntity> posMap = posService.getPosesFromReceiptBillsAndCardTypeId(
                billRequests,
                savedReceipt.getCustomerCard().getCardType().getId()
        ).stream().collect(Collectors.toMap(PosEntity::getId, pos -> pos));

        Map<UUID, BillEntity> billMap = new HashMap<>();

        if (!ObjectUtils.isEmpty(savedReceipt.getBills())) {
            billMap.putAll(savedReceipt.getBills().stream().collect(Collectors.toMap(BillEntity::getId, bill -> bill)));
        }

        List<BillEntity> savedBills = new ArrayList<>();

        billRequests.forEach(request -> {
            BillEntity savedBill = billMap.getOrDefault(request.getBillId(), BillEntity.buildNewBill(savedReceipt));

            if (posMap.get(request.getPosId()).getMaxBillAmount() < request.getMoneyAmount()) {
                throw new InvalidDataException(ERROR_MSG_BILL_VALUE_EXCEED_POS_LIMIT);
            }

            savedBill.setMoneyAmount(request.getMoneyAmount());
            savedBill.setFee(request.getFee());
            savedBill.setEstimatedProfit(request.getEstimatedProfit());
            savedBill.setPos(posMap.get(request.getPosId()));

            savedBills.add(savedBill);
        });

        long currentTimeStamp = (new Date()).getTime();

        for (BillEntity savedBill: savedBills) {
            savedBill.setTimeStampSeq(currentTimeStamp);
            currentTimeStamp += 1;
        }

        if (ObjectUtils.isEmpty(savedReceipt.getBills())) {
            savedReceipt.setBills(savedBills);
        } else {
            savedReceipt.getBills().clear();
            savedReceipt.getBills().addAll(savedBills);
        }
    }

    public void deleteBills (List<BillEntity> bills) {
        if (bills.stream().anyMatch(bill -> !ObjectUtils.isEmpty(bill.getCode()))) {
            throw new InvalidDataException(ERROR_MSG_BILL_ALREADY_HAS_CODE);
        }

        billRepository.deleteAll(bills);
    }

    public List<BillEntity> getMatchingBills (GetMatchingBillsRequest request) {
        List<BillEntity> bills = billRepository.findByPos_IdAndCreatedDateBetweenOrderByCreatedDateAsc(request.getPosId(), request.getFromCreatedDate(), request.getToCreatedDate());

        List<BillEntity> responseList = new ArrayList<>();
        double moneyAmount = 0.0;

        for (BillEntity bill : bills) {
            moneyAmount += bill.getMoneyAmount();

            if (moneyAmount > request.getMoneyAmount()) {
                break;
            }

            responseList.add(bill);
        }

        return responseList;
    }

    public List<BillEntity> matchBill (MatchingBillRequest request) {
        List<BillEntity> bills = billRepository.findAllById(request.getBillIds());

        if (bills.size() != request.getBillIds().size()) {
            throw new EntityNotFoundException(ERROR_MSG_SOME_BILL_IDS_NOT_FOUND);
        }

        if (bills.stream().mapToDouble(BillEntity::getEstimatedProfit).sum() != request.getMoneyAmount() && !request.isBypassDifference()) {
            throw new InvalidDataException(ERROR_MSG_MONEY_AMOUNT_DOES_NOT_MATCH_BILL);
        }

        bills.forEach(bill -> {
           bill.setReturnedProfit(bill.getEstimatedProfit());
           bill.setReturnedTime(new Date());
        });

        return billRepository.saveAll(bills);
    }

    public void assignNewBillCodes (List<BillEntity> bills) {
        validateBillsForApproval(bills);

        bills.sort((a, b) -> Math.toIntExact(a.getTimeStampSeq() - b.getTimeStampSeq()));

        Map<UUID, String> billCodeMap = new HashMap<>();

        for (BillEntity bill: bills) {
            String newBillCode = getNewBillCode(
                    bill,
                    billCodeMap.get(bill.getPos().getId())
            );

            bill.setCode(newBillCode);

            billCodeMap.put(
                    bill.getPos().getId(),
                    newBillCode
            );
        }
    }

    private void validateBillsForApproval (List<BillEntity> bills) {
        if (CollectionUtils.isEmpty(bills)) {
            throw new InvalidDataException(ERROR_MSG_NO_BILL_TO_APPROVE);
        }

        if (bills.stream().anyMatch(bill -> !ObjectUtils.isEmpty(bill.getCode()))) {
            throw new InvalidDataException(ERROR_MSG_SOME_BILL_INVALID_TO_APPROVE);
        }
    }

    private String getNewBillCode(BillEntity bill, String latestBillCode) {
        if (latestBillCode == null) {
            Optional<BillEntity> latestBill =
                    billRepository.findFirstByCodeNotNullAndPosAndCreatedDateBetweenOrderByTimeStampSeqDesc(
                            bill.getPos(),
                            DateTimeUtils.atStartOfDay(new Date()),
                            DateTimeUtils.atEndOfDay(new Date())
                    );

            return BillCodeHandler.generateBillCode(
                    bill.getPos().getCode(),
                    latestBill.map(BillEntity::getCode).orElse(null)
            );
        }

        return BillCodeHandler.generateBillCode(
                bill.getPos().getCode(),
                latestBillCode
        );
    }
}
