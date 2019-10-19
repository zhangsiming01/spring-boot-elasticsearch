package com.zsm.springboot.elasticsearch.to;

/**
 * @author: zhangsiming
 * @Date: 2019-10-18 15:47
 * @Description: es数据
 */
public enum ErrorCodeConstants {
    /**
     * 错误码在工程中出现次数必须全局唯一
     */
    ;

    /**
     * 错误code
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    ErrorCodeConstants(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;

    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


    public String getErrorMessage(Object... args) {
        return String.format(errorMessage, args);
    }

    public void generateError(Result<?> result){
        result.setStatusCode(getErrorCode());
        result.setMessage(getErrorMessage());
    }
}
