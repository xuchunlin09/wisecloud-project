package com.wisecloud.mdm.exception;

import com.wisecloud.mdm.dto.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleBusinessException_returns_correct_status_and_message() {
        BusinessException ex = new BusinessException(409, "用户名已存在");

        ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessException(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(409);
        assertThat(response.getBody().message()).isEqualTo("用户名已存在");
        assertThat(response.getBody().data()).isNull();
    }

    @Test
    void handleWiseCloudException_returns_502_without_leaking_details() {
        WiseCloudApiException ex = new WiseCloudApiException(-1004, "SDK internal error detail");

        ResponseEntity<ApiResponse<Void>> response = handler.handleWiseCloudException(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(502);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(502);
        assertThat(response.getBody().message()).isEqualTo("WiseCloud 服务不可用");
        // Must NOT contain the SDK internal message
        assertThat(response.getBody().message()).doesNotContain("SDK internal");
        assertThat(response.getBody().data()).isNull();
    }

    @Test
    void handleValidation_returns_400_with_field_error_messages() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "email", "邮箱格式错误"));
        bindingResult.addError(new FieldError("request", "password", "密码长度不足"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiResponse<Void>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(400);
        assertThat(response.getBody().message()).contains("邮箱格式错误");
        assertThat(response.getBody().message()).contains("密码长度不足");
    }

    @Test
    void handleGeneral_returns_500_with_generic_message() {
        Exception ex = new RuntimeException("unexpected NPE");

        ResponseEntity<ApiResponse<Void>> response = handler.handleGeneral(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("服务器内部错误");
        // Must NOT leak the original exception message
        assertThat(response.getBody().message()).doesNotContain("NPE");
        assertThat(response.getBody().data()).isNull();
    }
}
