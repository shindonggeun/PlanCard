package com.ssafy.backend.domain.authentication.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OAuthError {
    ABNORMAL_ACCESS(HttpStatus.UNAUTHORIZED, "비정상적인 접근입니다."),
    NOT_FOUND_EMAIL(HttpStatus.BAD_REQUEST, "SNS 계정의 이메일을 찾을 수 없습니다."),
    MISS_MATCH_PROVIDER(HttpStatus.BAD_REQUEST, "다른 SNS 계정으로 회원가입이 되어 있습니다.");

    private final HttpStatus httpStatus;
    private final String errorMessage;
}
