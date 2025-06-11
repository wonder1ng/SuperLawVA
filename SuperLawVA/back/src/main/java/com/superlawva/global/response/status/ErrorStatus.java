package com.superlawva.global.response.status;

import lombok.Getter;

@Getter
public enum ErrorStatus {
    INVALID_OR_EXPIRED_TOKEN("INVALID_OR_EXPIRED_TOKEN", "JWT가 유효하지 않거나 만료되었습니다."),
    // 추가적인 에러 상태를 여기에 정의할 수 있음
    ;

    private final String code;
    private final String message;

    ErrorStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
