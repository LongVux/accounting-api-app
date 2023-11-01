package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.exceptions.DuplicatedValueException;
import com.outwork.accountingapiapp.models.entity.CustomerEntity;
import com.outwork.accountingapiapp.models.payload.requests.SaveCustomerRequest;
import com.outwork.accountingapiapp.repositories.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.UUID;

@Service
public class CustomerService {
    private static final String ERROR_MSG_CUSTOMER_PHONE_EXISTED = "Số điện thoại khách hàng đã tồn tại";
    private static final String ERROR_MSG_CUSTOMER_NATION_ID_EXISTED = "Số thẻ căn cước công dân đã tồn tại";

    @Autowired
    private CustomerRepository customerRepository;

    public CustomerEntity getCustomerById (@NotNull UUID id) {
        return customerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public CustomerEntity saveCustomerEntity (@Valid SaveCustomerRequest request, UUID id) {
        validateSaveCustomerRequest(request, id);

        CustomerEntity savedCustomer = ObjectUtils.isEmpty(id) ? new CustomerEntity() : getCustomerById(id);
        mapSaveCustomerRequestToEntity(request, savedCustomer);

        return customerRepository.save(savedCustomer);
    }

    public void deleteCustomerEntity (@NotNull UUID id) {
        customerRepository.deleteById(id);
    }

    public void mapSaveCustomerRequestToEntity (SaveCustomerRequest request, CustomerEntity customer) {
        customer.setName(request.getName());
        customer.setAddress(request.getAddress());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setNationalId(request.getNationalId());
        customer.setPercentageFee(request.getPercentageFee());
    }

    private void validateSaveCustomerRequest (SaveCustomerRequest request, UUID id) {
        if (customerRepository.existsByPhoneNumberAndIdNot(request.getPhoneNumber(), id)) {
            throw new DuplicatedValueException(ERROR_MSG_CUSTOMER_PHONE_EXISTED);
        }

        if (customerRepository.existsByNationalIdAndIdNot(request.getNationalId(), id)) {
            throw new DuplicatedValueException(ERROR_MSG_CUSTOMER_NATION_ID_EXISTED);
        }
    }
}
