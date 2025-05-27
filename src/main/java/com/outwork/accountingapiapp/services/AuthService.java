package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.configs.security.JwtTokenProvider;
import com.outwork.accountingapiapp.models.entity.UserEntity;
import com.outwork.accountingapiapp.models.payload.requests.LoginRequest;
import com.outwork.accountingapiapp.models.payload.requests.SignupRequest;
import com.outwork.accountingapiapp.models.payload.responses.UserDetail;
import com.outwork.accountingapiapp.models.security.SecuredUserDetails;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service

public class AuthService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private String SUCCESS_MSG_REGISTRATION_SUCCESSFULLY = "Đăng ký tài khoản thành công";

    @Autowired
    private UserService userService;

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
        userService.createUser(signUpRequest);

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

        UserDetail userDetail = UserDetail.toUserDetail(securedUserDetails.getUserEntity(), jwt);

        log.info("User login: {}", userDetail);

        return userDetail;
    }

}
