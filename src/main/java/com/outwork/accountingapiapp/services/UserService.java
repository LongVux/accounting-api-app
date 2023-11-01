package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.models.entity.UserEntity;
import com.outwork.accountingapiapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity findUserEntityByCode (String userCode) {
        return userRepository.findByCode(userCode).orElseThrow(() -> new UsernameNotFoundException(userCode));
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
