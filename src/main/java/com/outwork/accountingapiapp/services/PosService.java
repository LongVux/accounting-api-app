package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.constants.PosStatusEnum;
import com.outwork.accountingapiapp.exceptions.DuplicatedValueException;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.CardTypeEntity;
import com.outwork.accountingapiapp.models.entity.PosCardFeeEntity;
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
    public static final String ERROR_MSG_POS_NAME_EXISTED = "Tên POS đã tồn tại";
    public static final String ERROR_MSG_POS_BANK_ACCOUNT_EXISTED = "Tài khoản ngân hàng của POS đã tồn tại";
    public static final String ERROR_MSG_THE_POS_NOT_SUPPORT_CARD = "POS %s không hỗ trợ loại thẻ này";
    public static final String ERROR_MSG_ONLY_POSES_SUPPORT_GIVEN_CARD = "Trong bill, chỉ có các POS sau hỗ trợ loại thẻ này: %s";
    public static final String ERROR_MSG_POS_HAS_NO_CARD_TYPE = "POS phải hỗ trợ ít nhất một loại thẻ";

    public static final String ERROR_MSG_CANNOT_DELETE = "Dữ liệu này đã được sử dụng trong hệ thống, không thể xóa!";

    @Autowired
    private PosRepository posRepository;

    @Autowired
    private PosCardFeeService posCardFeeService;

    @Autowired
    private BranchService branchService;

    public PosEntity getPosById (@NotNull UUID id) {
        return posRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public Page<PosTableItem> getPosTableItems (GetPosTableItemRequest request) {
        return posRepository.findAll(request, request.retrievePageConfig());
    }

    public List<SuggestedPos> searchPosByCodeAndBranch (@Size(min = 2) String searchKey, UUID branchId) {
        return posRepository.findByCodeContainsIgnoreCaseAndPosStatusAndBranch_Id(searchKey, PosStatusEnum.AVAILABLE, branchId);
    }

    public List<PosEntity> getPosesFromReceiptBillsAndCardTypeId (List<ReceiptBill> receiptBills, @NotNull UUID cardTypeId) {
        Set<UUID> requestedPosIds = new HashSet<>(receiptBills.stream().map(ReceiptBill::getPosId).toList());
        List<PosEntity> poses = findPosesByIdsAndSupportedCardId(requestedPosIds.stream().toList(), cardTypeId);

        if (poses.size() < requestedPosIds.size()) {
            throw new EntityNotFoundException(
                    String.format(
                            ERROR_MSG_ONLY_POSES_SUPPORT_GIVEN_CARD,
                            String.join(DataFormat.COMMA_SEPARATOR, poses.stream().map(PosEntity::getCode).toList())
                    )
            );
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
        try {
            posRepository.deleteById(id);
        } catch (Exception e) {
            throw new InvalidDataException(ERROR_MSG_CANNOT_DELETE);
        }
    }

    public double getPosFeeByCardType (PosEntity pos, CardTypeEntity cardType) {
        for (PosCardFeeEntity posCardFee : pos.getSupportedCardTypes()) {
            if (posCardFee.getCardType().getId().equals(cardType.getId())) {
                return posCardFee.getPosCardFee();
            }
        }

        throw new EntityNotFoundException(
                String.format(
                        ERROR_MSG_THE_POS_NOT_SUPPORT_CARD,
                        pos.getCode()
                )
        );
    }

    private void validateSavePosRequest (SavePosRequest request, UUID id) {
        if (posRepository.existsByCodeIgnoreCaseAndIdNot(request.getCode(), Optional.ofNullable(id).orElse(UUID.randomUUID()))) {
            throw new DuplicatedValueException(ERROR_MSG_POS_CODE_EXISTED);
        }

        if (posRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), Optional.ofNullable(id).orElse(UUID.randomUUID()))) {
            throw new DuplicatedValueException(ERROR_MSG_POS_NAME_EXISTED);
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
        pos.setNote(request.getNote());
        pos.setBranch(branchService.getBranchById(request.getBranchId()));

        posCardFeeService.buildPosCardFeesForPos(request.getSupportedCardTypes(), pos);

    }

    private List<PosEntity> findPosesByIdsAndSupportedCardId (List<UUID> posIds, @NotNull UUID cardTypeId) {
        return posRepository.findByIdInAndSupportedCardTypes_CardType_Id(posIds, cardTypeId);
    }
}
