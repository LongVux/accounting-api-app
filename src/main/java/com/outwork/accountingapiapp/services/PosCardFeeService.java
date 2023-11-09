package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.models.entity.*;
import com.outwork.accountingapiapp.models.payload.requests.SupportedCardType;
import com.outwork.accountingapiapp.repositories.PosCardFeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
public class PosCardFeeService {
    public static final String ERROR_MSG_SOME_CARD_TYPES_ID_NOT_EXISTED = "Một số ID loại thẻ không tồn tại";
    @Autowired
    private PosCardFeeRepository posCardFeeRepository;

    @Autowired
    private CardTypeService cardTypeService;

    public void buildPosCardFeesForPos (List<SupportedCardType> requests, PosEntity pos) {
        if (CollectionUtils.isEmpty(requests)) {
            return;
        }

        List<CardTypeEntity> cardTypes = cardTypeService.findCardTypesByIds(requests.stream().map(SupportedCardType::getCardTypeId).toList());

        if (requests.size() > cardTypes.size()) {
            throw new EntityNotFoundException(ERROR_MSG_SOME_CARD_TYPES_ID_NOT_EXISTED);
        }

        Map<UUID, PosCardFeeEntity> posCardFeeMap = new HashMap<>();

        requests.forEach(supportedCardType -> {
            PosCardFeeEntity newPosCardFee = new PosCardFeeEntity();

            if (!ObjectUtils.isEmpty(pos.getId())) {
                newPosCardFee.setId(UUID.randomUUID());
                newPosCardFee.setPos(pos);
            }

            newPosCardFee.setPosCardFee(supportedCardType.getPosCardFee());
            posCardFeeMap.put(supportedCardType.getCardTypeId(), newPosCardFee);
        });

        cardTypes.forEach(cardType -> {
            if (posCardFeeMap.containsKey(cardType.getId())) {
                posCardFeeMap.get(cardType.getId()).setCardType(cardType);
            }
        });

        if (ObjectUtils.isEmpty(pos.getSupportedCardTypes())) {
            pos.setSupportedCardTypes( posCardFeeMap.values().stream().toList());
        } else {
            pos.getSupportedCardTypes().clear();
            pos.getSupportedCardTypes().addAll( posCardFeeMap.values().stream().toList());
        }
    }
}
