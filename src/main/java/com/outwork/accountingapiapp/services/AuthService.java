package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.configs.security.JwtTokenProvider;
import com.outwork.accountingapiapp.exceptions.DuplicatedValueException;
import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.models.entity.RoleEntity;
import com.outwork.accountingapiapp.models.entity.UserEntity;
import com.outwork.accountingapiapp.models.payload.requests.LoginRequest;
import com.outwork.accountingapiapp.models.payload.requests.SignupRequest;
import com.outwork.accountingapiapp.models.payload.responses.UserDetail;
import com.outwork.accountingapiapp.models.security.SecuredUserDetails;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService implements UserDetailsService {
    private String ERROR_MSG_USER_CODE_EXISTED = "Mã người dùng đã tồn tại";
    private String ERROR_MSG_EMAIL_EXISTED = "Email đã tồn tại";
    private String ERROR_MSG_PHONE_NUMBER_EXISTED = "Số điện thoại đã tồn tại";

    private String SUCCESS_MSG_REGISTRATION_SUCCESSFULLY = "Đăng ký tài khoản thành công";

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public SecuredUserDetails loadUserByUsername(String userCode) throws UsernameNotFoundException {
        UserEntity userEntity = userService.getUserEntityByCode(userCode);
        return new SecuredUserDetails(userEntity);
    }

    public String registerUser (@Valid SignupRequest signUpRequest) {
        userService.createUser(signUpRequest, passwordEncoder.encode(signUpRequest.getPassword()));

        return SUCCESS_MSG_REGISTRATION_SUCCESSFULLY;
    }

    public UserDetail login (@Valid LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getCode(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.createToken(authentication);
        SecuredUserDetails securedUserDetails = (SecuredUserDetails) authentication.getPrincipal();

        return UserDetail.toUserDetail(securedUserDetails.getUserEntity(), jwt);
    }
}
