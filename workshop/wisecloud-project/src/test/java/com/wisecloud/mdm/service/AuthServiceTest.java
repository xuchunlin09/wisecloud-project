package com.wisecloud.mdm.service;

import com.wisecloud.mdm.dto.request.LoginRequest;
import com.wisecloud.mdm.dto.request.RegisterRequest;
import com.wisecloud.mdm.dto.response.LoginResponse;
import com.wisecloud.mdm.entity.User;
import com.wisecloud.mdm.exception.BusinessException;
import com.wisecloud.mdm.repository.UserRepository;
import com.wisecloud.mdm.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_success() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");

        authService.register(new RegisterRequest("newuser", "test@example.com", "password123"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("newuser");
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
        assertThat(saved.getPasswordHash()).isEqualTo("hashed_password");
    }

    @Test
    void register_duplicateUsername_throws409() {
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(
                new RegisterRequest("existing", "test@example.com", "password123")))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getHttpStatus()).isEqualTo(409);
                    assertThat(be.getMessage()).isEqualTo("用户名已存在");
                });

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("hashed");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtTokenProvider.generateToken(1L)).thenReturn("jwt_token");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(86400000L);

        LoginResponse response = authService.login(new LoginRequest("testuser", "password123"));

        assertThat(response.token()).isEqualTo("jwt_token");
        assertThat(response.expiresIn()).isEqualTo(86400);
    }

    @Test
    void login_userNotFound_throws401() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("unknown", "password123")))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getHttpStatus()).isEqualTo(401);
                    assertThat(be.getMessage()).isEqualTo("用户名或密码错误");
                });
    }

    @Test
    void login_wrongPassword_throws401() {
        User user = new User();
        user.setId(1L);
        user.setPasswordHash("hashed");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("testuser", "wrongpass")))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getHttpStatus()).isEqualTo(401);
                    assertThat(be.getMessage()).isEqualTo("用户名或密码错误");
                });
    }
}
