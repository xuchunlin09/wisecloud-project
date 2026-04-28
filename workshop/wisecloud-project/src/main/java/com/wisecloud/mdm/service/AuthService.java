package com.wisecloud.mdm.service;

import com.wisecloud.mdm.dto.request.LoginRequest;
import com.wisecloud.mdm.dto.request.RegisterRequest;
import com.wisecloud.mdm.dto.response.LoginResponse;
import com.wisecloud.mdm.entity.User;
import com.wisecloud.mdm.exception.BusinessException;
import com.wisecloud.mdm.repository.UserRepository;
import com.wisecloud.mdm.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(409, "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        String token = jwtTokenProvider.generateToken(user.getId());
        long expiresIn = jwtTokenProvider.getExpirationMs() / 1000;
        return new LoginResponse(token, expiresIn);
    }
}
