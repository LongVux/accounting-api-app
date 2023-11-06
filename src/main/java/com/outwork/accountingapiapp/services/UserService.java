package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.models.entity.UserEntity;
import com.outwork.accountingapiapp.models.payload.requests.GetUserTableItemRequest;
import com.outwork.accountingapiapp.models.payload.responses.UserTableItem;
import com.outwork.accountingapiapp.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity findUserEntityByCode (String userCode) {
        return userRepository.findByCode(userCode).orElseThrow(() -> new UsernameNotFoundException(userCode));
    }

    public Page<UserTableItem> getUserTableItems (GetUserTableItemRequest request) {
        return userRepository.findAll(request, request.retrievePageConfig()).map(UserTableItem::new);
    }

    public UserEntity getUserEntityById (@NotNull UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public boolean isUserCodeExisted (String userCode) {
        return userRepository.existsByCode(userCode);
    }

    public boolean isEmailExisted (String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isPhoneNumberExisted (String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public UserEntity saveUserEntity (UserEntity userEntity) {
        return userRepository.save(userEntity);
    }
}
