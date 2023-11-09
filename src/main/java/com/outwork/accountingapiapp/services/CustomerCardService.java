package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.exceptions.DuplicatedValueException;
import com.outwork.accountingapiapp.models.entity.CardTypeEntity;
import com.outwork.accountingapiapp.models.entity.CustomerCardEntity;
import com.outwork.accountingapiapp.models.entity.CustomerEntity;
import com.outwork.accountingapiapp.models.payload.requests.GetCustomerCardTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveCustomerCardRequest;
import com.outwork.accountingapiapp.models.payload.responses.CustomerCardTableItem;
import com.outwork.accountingapiapp.repositories.CustomerCardRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerCardService {
    public static final String ERROR_MSG_CUSTOMER_CARD_NUMBER_EXISTED = "Số tài khoản thẻ đã tồn tại";

    @Autowired
    private CustomerCardRepository customerCardRepository;

    @Autowired
    private CardTypeService cardTypeService;

    @Autowired
    private CustomerService customerService;

    public Page<CustomerCardTableItem> getCustomerCardTableItems (GetCustomerCardTableItemRequest request) {
        return customerCardRepository.findAll(request, request.retrievePageConfig());
    }

    public CustomerCardEntity getCustomerCardById (@NotNull UUID id) {
        return customerCardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public List<CustomerCardEntity> findCustomerCardByCustomerId (@NotNull UUID customerId) {
        return customerCardRepository.findByCustomer_Id(customerId);
    }

    public CustomerCardEntity saveCustomerCard (@Valid SaveCustomerCardRequest request, UUID id) {
        validateSaveCustomerCardRequest(request, id);

        CustomerCardEntity savedCustomerCard = ObjectUtils.isEmpty(id) ? new CustomerCardEntity() : getCustomerCardById(id);
        mapSaveCustomerCardRequestToEntity(request, savedCustomerCard);

        return customerCardRepository.save(savedCustomerCard);
    }

    public void deleteCustomerCard (@NotNull UUID id) {
        customerCardRepository.deleteById(id);
    }

    public void mapSaveCustomerCardRequestToEntity (SaveCustomerCardRequest request, CustomerCardEntity customerCard) {
        CardTypeEntity cardType = cardTypeService.getCardTypeById(request.getCardTypeId());
        CustomerEntity customer = customerService.getCustomerById(request.getCustomerId());

        customerCard.setName(request.getName());
        customerCard.setCardType(cardType);
        customerCard.setAccountNumber(request.getAccountNumber());
        customerCard.setBank(request.getBank());
        customerCard.setPaymentLimit(request.getPaymentLimit());
        customerCard.setPaymentDueDate(request.getPaymentDueDate());
        customerCard.setCustomer(customer);
    }

    private void validateSaveCustomerCardRequest (SaveCustomerCardRequest request, UUID id) {
        if (customerCardRepository.existsByAccountNumberAndBankIgnoreCaseAndIdNot(request.getAccountNumber(), request.getBank(), id)) {
            throw new DuplicatedValueException(ERROR_MSG_CUSTOMER_CARD_NUMBER_EXISTED);
        }
    }
}
