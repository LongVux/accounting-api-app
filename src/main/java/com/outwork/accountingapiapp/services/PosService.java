package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.constants.PosStatusEnum;
import com.outwork.accountingapiapp.exceptions.DuplicatedValueException;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.PosEntity;
import com.outwork.accountingapiapp.models.payload.requests.GetPosTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.ReceiptBill;
import com.outwork.accountingapiapp.models.payload.requests.SavePosRequest;
import com.outwork.accountingapiapp.models.payload.responses.PosTableItem;
import com.outwork.accountingapiapp.models.payload.responses.SuggestedPos;
import com.outwork.accountingapiapp.repositories.PosRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
public class PosService {

    public static final String ERROR_MSG_POS_CODE_EXISTED = "Mã POS đã tồn tại";
    public static final String ERROR_MSG_POS_BANK_ACCOUNT_EXISTED = "Tài khoản ngân hàng của POS đã tồn tại";
    public static final String ERROR_MSG_SOME_POS_NOT_SUPPORT_CARD = "Một số POS không hỗ trợ loại thẻ này";
    public static final String ERROR_MSG_POS_HAS_NO_CARD_TYPE = "POS phải hỗ trợ ít nhất một loại thẻ";

    @Autowired
    private PosRepository posRepository;

    @Autowired
    private PosCardFeeService posCardFeeService;

    public PosEntity getPosById (@NotNull UUID id) {
        return posRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public Page<PosTableItem> getPosTableItems (GetPosTableItemRequest request) {
        return posRepository.findAll(request, request.retrievePageConfig());
    }

    public List<SuggestedPos> searchPosByCode (@Size(min = 2) String searchKey) {
        return posRepository.findByCodeContainsIgnoreCaseAndPosStatus(searchKey, PosStatusEnum.AVAILABLE);
    }

    public List<PosEntity> getPosesFromReceiptBillsAndCardTypeId (List<ReceiptBill> receiptBills, @NotNull UUID cardTypeId) {
        Set<UUID> requestedPosIds = new HashSet<>(receiptBills.stream().map(ReceiptBill::getPosId).toList());
        List<PosEntity> poses = findPosesByIdsAndSupportedCardId(requestedPosIds.stream().toList(), cardTypeId);

        if (poses.size() < requestedPosIds.size()) {
            throw new EntityNotFoundException(ERROR_MSG_SOME_POS_NOT_SUPPORT_CARD);
        }

        return poses;
    }

    public PosEntity savePos (@Valid SavePosRequest request, UUID id) {
        validateSavePosRequest(request, id);

        PosEntity savedPos = ObjectUtils.isEmpty(id) ? new PosEntity() : getPosById(id);

        mapSavePosRequestToEntity(request, savedPos);

        validatePosForSave(savedPos);

        return posRepository.save(savedPos);
    }

    public void deletePos (@NotNull UUID id) {
        posRepository.deleteById(id);
    }

    private void validateSavePosRequest (SavePosRequest request, UUID id) {
        if (posRepository.existsByCodeIgnoreCaseAndIdNot(request.getCode(), Optional.ofNullable(id).orElse(UUID.randomUUID()))) {
            throw new DuplicatedValueException(ERROR_MSG_POS_CODE_EXISTED);
        }

        if (posRepository.existsByAccountNumberAndBankIgnoreCaseAndIdNot(request.getAccountNumber(), request.getBank(), Optional.ofNullable(id).orElse(UUID.randomUUID()))) {
            throw new DuplicatedValueException(ERROR_MSG_POS_BANK_ACCOUNT_EXISTED);
        }
    }

    private void validatePosForSave (PosEntity pos) {
        if (CollectionUtils.isEmpty(pos.getSupportedCardTypes())) {
            throw new InvalidDataException(ERROR_MSG_POS_HAS_NO_CARD_TYPE);
        }
    }

    private void mapSavePosRequestToEntity (SavePosRequest request, PosEntity pos) {
        pos.setCode(request.getCode());
        pos.setName(request.getName());
        pos.setPosStatus(request.getPosStatus());
        pos.setAddress(request.getAddress());
        pos.setAccountNumber(request.getAccountNumber());
        pos.setBank(request.getBank());
        pos.setMaxBillAmount(request.getMaxBillAmount());

        posCardFeeService.buildPosCardFeesForPos(request.getSupportedCardTypes(), pos);

    }

    private List<PosEntity> findPosesByIdsAndSupportedCardId (List<UUID> posIds, @NotNull UUID cardTypeId) {
        return posRepository.findByIdInAndSupportedCardTypes_CardType_Id(posIds, cardTypeId);
    }
}
