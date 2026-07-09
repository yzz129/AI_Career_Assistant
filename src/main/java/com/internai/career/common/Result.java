package com.internai.career.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> ok() {
        return build(ResultCode.SUCCESS, null);
    }

    public static <T> Result<T> ok(T data) {
        return build(ResultCode.SUCCESS, data);
    }

    public static <T> Result<T> fail(String message) {
        Result<T> result = build(ResultCode.ERROR, null);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> fail(ResultCode code, String message) {
        Result<T> result = build(code, null);
        result.setMessage(message == null ? code.getMessage() : message);
        return result;
    }

    private static <T> Result<T> build(ResultCode code, T data) {
        Result<T> result = new Result<>();
        result.setCode(code.getCode());
        result.setMessage(code.getMessage());
        result.setData(data);
        return result;
    }
}
