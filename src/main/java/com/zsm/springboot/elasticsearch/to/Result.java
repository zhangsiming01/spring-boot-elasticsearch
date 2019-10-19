package com.zsm.springboot.elasticsearch.to;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author: zhangsiming
 * @Date: 2019-10-18 15:47
 * @Description: es 接口定义类
 */
@ApiModel(value="返回实体")
public class Result<T> implements Serializable {
    private static final String SUCESSCODE = "SYS000";

    private static final long serialVersionUID = 1L;

    private boolean status = false;
    @ApiModelProperty("返回信息")
    private String message;
    @ApiModelProperty("记录总数")
    private long total;
    @ApiModelProperty("数据实体")
    private T result;
    @ApiModelProperty("状态码")
    private String statusCode;

    public static <T> Result<T> error(ErrorCodeConstants constants) {
        return new Result<>(constants.getErrorMessage(), null, constants.getErrorCode());
    }

    public static <T> Result<T> error(ErrorCodeConstants constants, String errorDetailMsg) {
        return new Result<>(constants.getErrorMessage() + ",详细错误信息:" + errorDetailMsg, null, constants.getErrorCode());
    }


    public static <T> Result<T> error(String message) {
        return new Result<>(message, null, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, 0, "操作成功", data, SUCESSCODE);
    }

    public static <T> Result<T> success(T data, long total) {
        return new Result<>(true, total, "操作成功", data, SUCESSCODE);
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<>(true, 0, message, data, SUCESSCODE);
    }

    public Result() {
        super();
    }

    public Result(String message, T result, String statusCode) {
        this.message = message;
        this.result = result;
        this.statusCode = statusCode;
    }

    public Result(boolean success, long total, String message, T result, String statusCode) {
        this.status = success;
        this.total = total;
        this.message = message;
        this.result = result;
        this.statusCode = statusCode;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
