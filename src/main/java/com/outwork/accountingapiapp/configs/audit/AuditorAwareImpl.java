package com.outwork.accountingapiapp.configs.audit;

import com.outwork.accountingapiapp.models.entity.UserEntity;
import com.outwork.accountingapiapp.models.security.SecuredUserDetails;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    private static final String ERROR_MSG_CANNOT_RETRIEVE_AUTH_INFO = "Không trích xuất được thông tin xác thực";
    @Override
    public @NotNull Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return Optional.ofNullable(((SecuredUserDetails) authentication.getPrincipal()).getUsername());
    }

    public static UserEntity getUserFromSecurityContext () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthorizationServiceException(ERROR_MSG_CANNOT_RETRIEVE_AUTH_INFO);
        }

        return ((SecuredUserDetails) authentication.getPrincipal()).getUserEntity();
    }
}
