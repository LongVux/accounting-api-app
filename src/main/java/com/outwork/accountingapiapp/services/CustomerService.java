package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.exceptions.DuplicatedValueException;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.CustomerEntity;
import com.outwork.accountingapiapp.models.payload.requests.GetCustomerTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveCustomerRequest;
import com.outwork.accountingapiapp.models.payload.responses.CustomerTableItem;
import com.outwork.accountingapiapp.models.payload.responses.SuggestedCustomer;
import com.outwork.accountingapiapp.repositories.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {
    public static final String ERROR_MSG_CUSTOMER_PHONE_EXISTED = "Số điện thoại khách hàng đã tồn tại";
    public static final String ERROR_MSG_CUSTOMER_NATION_ID_EXISTED = "Số thẻ căn cước công dân đã tồn tại";
    public static final String ERROR_MSG_CANNOT_DELETE = "Dữ liệu này đã được sử dụng trong hệ thống, không thể xóa!";

    @Autowired
    private CustomerRepository customerRepository;

    public CustomerEntity getCustomerById (@NotNull UUID id) {
        return customerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public Page<CustomerTableItem> getCustomerTableItems (GetCustomerTableItemRequest request) {
        return customerRepository.findAll(request, request.retrievePageConfig());
    }

    public List<SuggestedCustomer> findCustomersByName (@Size(min = 2) String name) {
        return customerRepository.findByNameLikeIgnoreCase(String.format(DataFormat.LIKE_QUERY_FORMAT, name));
    }

    public CustomerEntity saveCustomerEntity (@Valid SaveCustomerRequest request, UUID id) {
        validateSaveCustomerRequest(request, id);

        CustomerEntity savedCustomer = ObjectUtils.isEmpty(id) ? new CustomerEntity() : getCustomerById(id);
        mapSaveCustomerRequestToEntity(request, savedCustomer);

        return customerRepository.save(savedCustomer);
    }

    public void deleteCustomerEntity (@NotNull UUID id) {
        try {
            customerRepository.deleteById(id);
        } catch (Exception e) {
            throw new InvalidDataException(ERROR_MSG_CANNOT_DELETE);
        }

    }

    public void mapSaveCustomerRequestToEntity (SaveCustomerRequest request, CustomerEntity customer) {
        customer.setName(request.getName());
        customer.setAddress(request.getAddress());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setNationalId(request.getNationalId());
        customer.setPercentageFee(request.getPercentageFee());
    }

    private void validateSaveCustomerRequest (SaveCustomerRequest request, UUID id) {
        if (customerRepository.existsByPhoneNumberAndIdNot(request.getPhoneNumber(), Optional.ofNullable(id).orElse(UUID.randomUUID()))) {
            throw new DuplicatedValueException(ERROR_MSG_CUSTOMER_PHONE_EXISTED);
        }

        if (customerRepository.existsByNationalIdAndIdNot(request.getNationalId(), Optional.ofNullable(id).orElse(UUID.randomUUID()))) {
            throw new DuplicatedValueException(ERROR_MSG_CUSTOMER_NATION_ID_EXISTED);
        }
    }
}
