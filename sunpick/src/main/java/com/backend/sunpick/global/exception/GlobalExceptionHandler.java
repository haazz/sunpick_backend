package com.backend.sunpick.global.exception;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리
 * <p>
 * 컨트롤러에서 발생한 예외를 가로채 간단한 HTTP 상태 코드와 메시지로 변환
 * </p>
 *
 * @author haazz
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private String messageOrDefault(String message, String defaultMessage) {
        if (message == null || message.isBlank()) {
            return defaultMessage;
        }
        return message;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(messageOrDefault(e.getMessage(), "잘못된 요청입니다."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Optional<String> fieldMsg = e.getBindingResult().getFieldErrors().stream()
            .map(fe -> messageOrDefault(fe.getDefaultMessage(), "유효성 검증 오류"))
            .findFirst();

        Optional<String> globalMsg = e.getBindingResult().getGlobalErrors().stream()
            .map(ge -> messageOrDefault(ge.getDefaultMessage(), "유효성 검증 오류"))
            .findFirst();

        String errorMessage = fieldMsg
            .or(() -> globalMsg)  // fieldMsg -> globalMsg -> default
            .orElse("요청 값 검증에 실패했습니다.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElement(NoSuchElementException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(messageOrDefault(e.getMessage(), "요청한 리소스를 찾을 수 없습니다."));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException e) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(messageOrDefault(e.getMessage(), "잘못된 상태로 요청을 처리할 수 없습니다."));
    }

    @ExceptionHandler({
        AccessDeniedException.class,
        AuthorizationDeniedException.class,
        SecurityException.class
    })
    public ResponseEntity<String> handleAccessDenied(Exception e) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(messageOrDefault(e.getMessage(), "접근 권한이 없습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(messageOrDefault(e.getMessage(), "서버 에러가 발생했습니다."));
    }
}
