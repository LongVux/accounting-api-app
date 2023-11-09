package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.exceptions.DuplicatedValueException;
import com.outwork.accountingapiapp.models.entity.CardTypeEntity;
import com.outwork.accountingapiapp.models.payload.requests.SaveCardTypeRequest;
import com.outwork.accountingapiapp.repositories.CardTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;

@Service
public class CardTypeService {
    public static final String ERROR_MSG_CARD_TYPE_NAME_EXISTED = "Tên loại thẻ đã tồn tại";

    @Autowired
    private CardTypeRepository cardTypeRepository;

    public List<CardTypeEntity> getCardTypes () {
        return cardTypeRepository.findAll();
    }

    public CardTypeEntity getCardTypeById (@NotNull UUID id) {
        return cardTypeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public List<CardTypeEntity> findCardTypesByIds (List<UUID> ids) {
        return cardTypeRepository.findAllById(ids);
    }

    public CardTypeEntity saveCardType (@Valid SaveCardTypeRequest request, UUID id) {
        if (cardTypeRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new DuplicatedValueException(ERROR_MSG_CARD_TYPE_NAME_EXISTED);
        }

        CardTypeEntity savedCardType = ObjectUtils.isEmpty(id) ? new CardTypeEntity() : getCardTypeById(id);
        savedCardType.setName(request.getName());

        return cardTypeRepository.save(savedCardType);
    }

    public void deleteCardTypeById (@NotNull UUID id) {
        cardTypeRepository.deleteById(id);
    }
}
