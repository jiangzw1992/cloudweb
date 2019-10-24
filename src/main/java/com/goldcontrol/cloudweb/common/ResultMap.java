package com.goldcontrol.cloudweb.common;


import org.springframework.stereotype.Component;
import java.util.HashMap;

@Component
public class ResultMap extends HashMap<String, Object> {

    private boolean ok;

    private Integer code;

    public ResultMap() {
    }

    public ResultMap success() {
        this.put("ok", true);
        this.put("code", 200);
        return this;
    }

    public ResultMap fail() {
        this.put("ok", false);
        this.put("code", 400);
        return this;
    }

    public ResultMap warn() {
        this.put("ok", true);
        this.put("code", 405);
        return this;
    }

    public ResultMap code(int code) {
        this.put("code", code);
        return this;
    }

    public ResultMap message(String message) {
        this.put("message", message);
        return this;
    }

    public ResultMap setData(Object data) {
        this.put("data", data);
        return this;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
