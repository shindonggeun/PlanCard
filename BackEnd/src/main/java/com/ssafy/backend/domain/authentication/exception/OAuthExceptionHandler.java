package com.ssafy.backend.domain.authentication.exception;

import com.ssafy.backend.domain.member.exception.MemberException;
import com.ssafy.backend.global.common.dto.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class OAuthExceptionHandler {
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<Message<Void>> memberException(MemberException e) {
        log.error("{} is occurred", e.getMessage());
        return ResponseEntity.ok().body(Message.fail(null, e.getErrorMessage()));
    }
}
