package com.dtazza.todo.common.model;

import java.io.Serializable;

/**
 * 统一错误响应体，包含 errorCode、errorMessage、用户提示信息三要素。
 */
public class ErrorResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String errorCode;

    private String errorMessage;

    private String userTip;

    public ErrorResponse() {
    }

    public ErrorResponse(String errorCode, String errorMessage, String userTip) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.userTip = userTip;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getUserTip() {
        return userTip;
    }

    public void setUserTip(String userTip) {
        this.userTip = userTip;
    }

    @Override
    public String toString() {
        return "ErrorResponse{"
                + "errorCode='" + errorCode + '\''
                + ", errorMessage='" + errorMessage + '\''
                + ", userTip='" + userTip + '\''
                + '}';
    }
}
