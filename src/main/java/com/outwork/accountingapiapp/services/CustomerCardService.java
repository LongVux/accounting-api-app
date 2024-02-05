package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.configs.audit.AuditorAwareImpl;
import com.outwork.accountingapiapp.constants.AccountEntryStatusEnum;
import com.outwork.accountingapiapp.exceptions.DuplicatedValueException;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.*;
import com.outwork.accountingapiapp.models.payload.requests.AdjustPrePaidFeeRequest;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerCardService {
    public static final String ERROR_MSG_CUSTOMER_CARD_NUMBER_EXISTED = "Số tài khoản thẻ đã tồn tại";
    public static final String ERROR_MSG_CANNOT_DELETE = "Dữ liệu này đã được sử dụng trong hệ thống, không thể xóa!";
    public static final String ERROR_MSG_CARD_EXPIRY_TIME_MUST_BE_IN_FUTURE = "Thời hạn của thẻ chỉ có thể nhận giá trị ngày tương lai";
    public static final String ERROR_MSG_USER_CANNOT_MODIFY_PREPAID_FEE = "Chỉ có người dùng $s mới có thể thay đổi giá trị phí đã ứng của thẻ này";
    @Autowired
    private CustomerCardRepository customerCardRepository;

    @Autowired
    private CardTypeService cardTypeService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BranchAccountEntryService branchAccountEntryService;

    public Page<CustomerCardTableItem> getCustomerCardTableItems (GetCustomerCardTableItemRequest request) {
        return customerCardRepository.findAll(request, request.retrievePageConfig());
    }

    public CustomerCardEntity getCustomerCardById (@NotNull UUID id) {
        return customerCardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public List<CustomerCardEntity> findNonExpiredCustomerCardByCustomerId (@NotNull UUID customerId) {
        return customerCardRepository.findByCustomer_IdAndExpiredDateGreaterThanEqual(customerId, new Date());
    }

    public CustomerCardEntity saveCustomerCard (@Valid SaveCustomerCardRequest request, UUID id) {
        CustomerCardEntity savedCustomerCard = ObjectUtils.isEmpty(id) ? new CustomerCardEntity() : getCustomerCardById(id);

        validateSaveCustomerCardRequest(request, savedCustomerCard);

        mapSaveCustomerCardRequestToEntity(request, savedCustomerCard);

        savedCustomerCard.setPrePaidFee(Optional.of(savedCustomerCard.getPrePaidFee()).orElse(0d));

        return customerCardRepository.save(savedCustomerCard);
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public CustomerCardEntity adjustPrePaidFee (AdjustPrePaidFeeRequest request) {
        UserEntity editor = AuditorAwareImpl.getUserFromSecurityContext();
        CustomerCardEntity customerCard = getCustomerCardById(request.getCustomerCardId());

        if (request.getPrePaidFee() != customerCard.getPrePaidFee()) {
            if (customerCard.getPrePaidFee() == 0 || ObjectUtils.nullSafeEquals(editor.getCode(), customerCard.getPrePaidFeeReceiverCode())) {

                branchAccountEntryService.createCardAdjustPrePaidFeeEntry(customerCard, request);

                customerCard.setPrePaidFee(request.getPrePaidFee());
                customerCard.setPrePaidFeeReceiverCode(editor.getCode());
            } else {
                throw new InvalidDataException(String.format(ERROR_MSG_USER_CANNOT_MODIFY_PREPAID_FEE, customerCard.getPrePaidFeeReceiverCode()));
            }
        }

       return customerCardRepository.save(customerCard);
    }

    public void deleteCustomerCard (@NotNull UUID id) {
        try {
            customerCardRepository.deleteById(id);
        } catch (Exception e) {
            throw new InvalidDataException(ERROR_MSG_CANNOT_DELETE);
        }
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
        customerCard.setExpiredDate(request.getExpiredDate());
        customerCard.setNote(request.getNote());
        customerCard.setCustomer(customer);
    }

    private void validateSaveCustomerCardRequest (SaveCustomerCardRequest request, CustomerCardEntity currentCard) {
        if (customerCardRepository.existsByAccountNumberAndBankIgnoreCaseAndIdNot(request.getAccountNumber(), request.getBank(), Optional.ofNullable(currentCard.getId()).orElse(UUID.randomUUID()))) {
            throw new DuplicatedValueException(ERROR_MSG_CUSTOMER_CARD_NUMBER_EXISTED);
        }

        if (request.getExpiredDate().before(new Date())) {
            throw new InvalidDataException(ERROR_MSG_CARD_EXPIRY_TIME_MUST_BE_IN_FUTURE);
        }
    }

    public CustomerCardEntity saveCustomerCardEntity(CustomerCardEntity customerCard) {
        return customerCardRepository.save(customerCard);
    }
}
