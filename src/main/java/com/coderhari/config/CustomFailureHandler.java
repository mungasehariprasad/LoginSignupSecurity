package com.coderhari.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.coderhari.entity.User;
import com.coderhari.repository.UserRepository;
import com.coderhari.service.UserService;
import com.coderhari.service.UserServiceImp;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        User user = userRepository.findByEmail(email);
        if (user != null) {
            if (user.isEnable()) {
                if (user.isAccountNonLocked()) {
                    if (user.getFailedAttempt() < UserServiceImp.ATTEMT_TIME - 1) {
                        userService.increaseFailedAttempt(user);

                    } else {
                        userService.lock(user);
                        exception = new LockedException("Your account is locked !! failed attemet 3 times");
                    }
                } else if (!user.isAccountNonLocked()) {
                    if (userService.unlockAccountTimeExpired(user)) {
                        exception = new LockedException("Account is unlockrd please try to login ");

                    } else {
                        exception = new LockedException("Account is loccked please try to login ");

                    }
                }

            } else {
                exception = new LockedException("Account is inactive..verify acount");
            }

        }

        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);
    }

}
