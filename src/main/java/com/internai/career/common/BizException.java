package com.internai.career.common;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {
    private final ResultCode code;

    public BizException(String message) {
        this(ResultCode.BAD_REQUEST, message);
    }

    public BizException(ResultCode code, String message) {
        super(message);
        this.code = code;
    }
}
