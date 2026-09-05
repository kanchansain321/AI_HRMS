package com.aihrms.aihrmsbackend.dto.response;

public class CustomResponse {

    private Integer status;

    private Object data;

    private String message;

    private Object error;

    private Integer size;


    public CustomResponse(Integer status, Object data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public CustomResponse() {
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
