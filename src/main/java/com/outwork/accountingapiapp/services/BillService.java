package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.constants.DataConstraint;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.BillEntity;
import com.outwork.accountingapiapp.models.entity.PosEntity;
import com.outwork.accountingapiapp.models.entity.ReceiptEntity;
import com.outwork.accountingapiapp.models.payload.requests.*;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BillService {
    public static final String ERROR_MSG_SOME_BILL_INVALID_TO_APPROVE = "Một số bill không hợp lệ để tạo bút toán";
    public static final String ERROR_MSG_NO_BILL_TO_APPROVE = "Không có bill để tạo bút toán";
    public static final String ERROR_MSG_BILL_VALUE_EXCEED_POS_LIMIT = "Giá trị Bill %.2f vượt quá giới hạn của POS %.2f";
    public static final String ERROR_MSG_BILL_FEE_EXCEED_TOTAL_MONEY = "Phí Bill %.2f vượt quá tổng giá trị cua Bill %.2f";
    public static final String ERROR_MSG_BILL_PROFIT_LOSES = "Lợi nhuân bill bị lỗ vì phí POS %s %.2f lớn hơn phí Bill %.2f";
    public static final String ERROR_MSG_SOME_BILL_IDS_NOT_FOUND = "Một số bill không tồn tại";
    public static final String ERROR_MSG_SOME_BILL_INVALID_TO_MATCH = "Một số bill chưa có mã để kết toán";
    public static final String ERROR_MSG_POS_DOES_NOT_SUPPORT_CARD = "Pos %s không hỗ trợ thanh toán thẻ loại này";
    public static final String ERROR_MSG_MATCHED_BILL_CAN_NOT_BE_UPDATE = "Bill đã khớp thì không thể cập nhật";
    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PosService posService;

    @Autowired
    private GeneralAccountEntryService generalAccountEntryService;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private Util util;

    public BillEntity getBillById (@NotNull UUID id) {
        return billRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public Page<BillTableItem> getBillTableItems (GetBillTableItemRequest request) {
        return billRepository.findAll(request, request.retrievePageConfig()).map(BillTableItem::new);
    }

    public List<BillTableItem> getAllBillTableItems (GetBillTableItemRequest request) {
        return billRepository.findAll(request, Pageable.unpaged()).map(BillTableItem::new).getContent();
    }

    public BillSumUpInfo getBillSumUpInfo (GetBillTableItemRequest request) {
        List<Double> result = util.getSumsBySpecifications(Collections.singletonList(request), BillEntity.getSumUpFields(), BillEntity.class);

        BillSumUpInfo sumUpInfo = new BillSumUpInfo();

        sumUpInfo.setTotalMoneyAmount(Optional.ofNullable(result.get(0)).orElse(0d));
        sumUpInfo.setTotalReturnFromBank(Optional.ofNullable(result.get(1)).orElse(0d));

        return sumUpInfo;
    }

    public void saveBillNote (SaveNoteRequest request) {
        BillEntity bill = getBillById(request.getId());
        bill.setNote(request.getNote());

        billRepository.save(bill);
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public List<BillEntity> updatePosFeeForBills (UpdatePosFeeForBillsRequest request) {
        List<BillEntity> bills = billRepository.findAllById(request.getBillIds());

        Map<UUID, ReceiptEntity> recalculatedReceipts = new HashMap<>();

        bills.forEach(bill -> {
            bill.setPosFeeStamp(request.getPosFeeStamp());
            recalculatedReceipts.put(bill.getReceipt().getId(), bill.getReceipt());

            if (!ObjectUtils.isEmpty(bill.getReturnFromBank())) {
                bill.setReturnFromBank(bill.getEstimatedReturnFromBank());
            }
        });

        receiptService.reCalculatedReceipts(recalculatedReceipts.values().stream().toList());

        return billRepository.saveAll(bills);
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public BillEntity updateBill (UpdateBillRequest request, UUID billId) {
        BillEntity bill = getBillById(billId);

//        if (!ObjectUtils.isEmpty(bill.getReturnedTime())) {
//            throw new InvalidDataException(ERROR_MSG_MATCHED_BILL_CAN_NOT_BE_UPDATE);
//        }

        if (!ObjectUtils.isEmpty(request.getPosId()) &&
                !request.getPosId().equals(bill.getPos().getId()) &&
                !ObjectUtils.isEmpty(bill.getCode())) {
            updateBillCode(request, bill);
        }

        if (bill.getPosFeeStamp() != request.getPosFeeStamp()) {
            bill.setPosFeeStamp(request.getPosFeeStamp());
            receiptService.reCalculatedReceipts(List.of(bill.getReceipt()));
        }

        return billRepository.save(bill);
    }

    private void updateBillCode (UpdateBillRequest request, BillEntity bill) {
        PosEntity pos = posService.getPosById(request.getPosId());

        bill.setPos(pos);
        bill.setCode(null);

        bill.setHistory(bill.getHistory() + (new Date()) + "-" + bill.getCode() + "\n");
        bill.setCode(getNewBillCode(bill, null));
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

            savedBill.setMoneyAmount(request.getMoneyAmount());
            savedBill.setFee(request.getFee());

            savedBill.setPos(posMap.get(request.getPosId()));

            double posFee = posService.getPosFeeByCardType(
                    savedBill.getPos(),
                    savedReceipt.getCustomerCard().getCardType()
            );

            if (Double.isNaN(posFee) || posFee == 0) {
                throw new InvalidDataException(String.format(ERROR_MSG_POS_DOES_NOT_SUPPORT_CARD, posMap.get(request.getPosId()).getCode()));
            }

            savedBill.setPosFeeStamp(posFee);

            validateReceiptBillForSave(savedBill);

            savedBills.add(savedBill);
        });

        long currentTimeStamp = (new Date()).getTime();

        for (BillEntity savedBill : savedBills) {
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
//        if (bills.stream().anyMatch(bill -> !ObjectUtils.isEmpty(bill.getCode()))) {
//            throw new InvalidDataException(ERROR_MSG_BILL_ALREADY_HAS_CODE);
//        }

        billRepository.deleteAll(bills);
    }

    public List<BillEntity> getMatchingBills (GetMatchingBillsRequest request) {
        List<BillEntity> bills = billRepository.findByPos_IdAndCreatedDateBetweenAndCodeNotNullAndReturnedTimeNullOrderByCreatedDateAscTimeStampSeqAsc(request.getPosId(), request.getFromCreatedDate(), request.getToCreatedDate());

        List<BillEntity> responseList = new ArrayList<>();
        double moneyAmount = 0d;

        for (BillEntity bill : bills) {
            if (moneyAmount + bill.getEstimatedReturnFromBank() > request.getMoneyAmount()) {
                continue;
            }

            moneyAmount += bill.getEstimatedReturnFromBank();

            responseList.add(bill);
        }

        return responseList;
    }

    public List<BillEntity> getPosFeeModifyingBills (GetPosFeeModifyingBillRequest request) {
       return billRepository.findByPos_IdAndCreatedDateBetweenOrderByCreatedDateAscTimeStampSeqAsc(request.getPosId(), request.getFromCreatedDate(), request.getToCreatedDate());
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public List<BillEntity> matchBill (MatchingBillRequest request) {
        List<BillEntity> bills = billRepository.findAllById(request.getBillIds());

        if (bills.size() != request.getBillIds().size()) {
            throw new EntityNotFoundException(ERROR_MSG_SOME_BILL_IDS_NOT_FOUND);
        }

        if (bills.stream().anyMatch(bill -> ObjectUtils.isEmpty(bill.getCode()))) {
            throw new InvalidDataException(ERROR_MSG_SOME_BILL_INVALID_TO_MATCH);
        }

        bills.forEach(bill -> {
           bill.setReturnFromBank(bill.getEstimatedReturnFromBank());
           bill.setReturnedTime(new Date());
        });

        generalAccountEntryService.generateGeneralAccountEntryFromMatchedBills(request, bills);
        receiptService.remarkReCalculateReceiptsProfitFromBillsMatch(bills);

        return billRepository.saveAll(bills);
    }

    public void approveBills (List<BillEntity> bills) {
        validateBillsForApproval(bills);

        bills.sort((a, b) -> Math.toIntExact(a.getTimeStampSeq() - b.getTimeStampSeq()));

        Map<UUID, String> billCodeMap = new HashMap<>();

        Date confirmedDate = new Date();

        for (BillEntity bill: bills) {
            bill.setConfirmedDate(confirmedDate);

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

    private void validateReceiptBillForSave (BillEntity bill) {
        if (bill.getFee() > bill.getMoneyAmount()) {
            throw new InvalidDataException(
                    String.format(
                            ERROR_MSG_BILL_FEE_EXCEED_TOTAL_MONEY,
                            bill.getFee(),
                            bill.getMoneyAmount()
                    )
            );
        }

        if (bill.getPos().getMaxBillAmount() < bill.getMoneyAmount()) {
            throw new InvalidDataException(
                    String.format(
                            ERROR_MSG_BILL_VALUE_EXCEED_POS_LIMIT,
                            bill.getMoneyAmount(),
                            bill.getPos().getMaxBillAmount()
                    )
            );
        }

        if (bill.getFee() < bill.getMoneyAmount() * bill.getPosFeeStamp() / 100 && !Pattern.matches(DataConstraint.COMPANY_CARD_REGEX, bill.getReceipt().getCustomerCard().getName())) {
            throw new InvalidDataException(
                    String.format(
                            ERROR_MSG_BILL_PROFIT_LOSES,
                            bill.getPos().getCode(),
                            bill.getPosFeeStamp() / 100 * bill.getMoneyAmount(),
                            bill.getFee()
                    )
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
                    billRepository.findFirstByCodeNotNullAndPosAndCreatedDateBetweenOrderByConfirmedDateDescTimeStampSeqDesc(
                            bill.getPos(),
                            DateTimeUtils.atStartOfDay(bill.getCreatedDate()),
                            DateTimeUtils.atEndOfDay(bill.getCreatedDate())
                    );

            return BillCodeHandler.generateBillCode(
                    bill.getPos().getCode(),
                    latestBill.map(BillEntity::getCode).orElse(null),
                    bill.getCreatedDate()
            );
        }

        return BillCodeHandler.generateBillCode(
                bill.getPos().getCode(),
                latestBillCode,
                bill.getCreatedDate()
        );
    }
}
