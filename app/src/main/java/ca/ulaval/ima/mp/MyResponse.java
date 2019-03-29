package ca.ulaval.ima.mp;

import okhttp3.Headers;

public class MyResponse {
    private String message;
    private Integer code;
    private Headers headers;
    private String body;

    MyResponse(String message, Integer code, Headers headers, String body) {
        this.message = message;
        this.code = code;
        this.headers = headers;
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
