package com.wisecloud.mdm.exception;

import com.wisecloud.mdm.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(ApiResponse.error(e.getHttpStatus(), e.getMessage()));
    }

    @ExceptionHandler(WiseCloudApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleWiseCloudException(WiseCloudApiException e) {
        log.error("WiseCloud API error: code={}, msg={}", e.getCode(), e.getMessage());
        return ResponseEntity.status(502)
                .body(ApiResponse.error(502, "WiseCloud 服务不可用"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "服务器内部错误"));
    }
}
