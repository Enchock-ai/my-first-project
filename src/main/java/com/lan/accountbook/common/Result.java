package com.lan.accountbook.common;

public class Result {
    private Integer code;
    private String msg;
    private Object data;

    // 成功响应（带数据）
    public static Result success(Object data) {
        Result r = new Result();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    // 成功响应（不带数据）
    public static Result success() {
        return success(null);
    }

    // 失败响应（只传消息，默认500状态码）
    public static Result error(String msg) {
        return error(500, msg);
    }

    // 失败响应（自定义状态码和消息）【新增方法】
    public static Result error(int code, String msg) {
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    // Getter 和 Setter
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}