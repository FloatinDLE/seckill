package org.seckill.dto;
//所有ajax的返回类型,封装json结果
public class SeckillResult <T>{//泛型
    private boolean success;//判断是否成功
    private T data;
    private String error;

    //成功，带数据
    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }
    //失败，传入错误信息
    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }
    //GetterandSetter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
