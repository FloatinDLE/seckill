package org.seckill.dto;

import org.seckill.entity.successKilled;
import org.seckill.enums.SeckillStateEnum;

//封装秒杀执行后结果
public class SeckillExecution {

    private  long seckillId;
    //秒杀执行结果状态
    private int state;
    //状态表示
    private String stateInfo;
    //秒杀成功对象
    private successKilled successKilled;

    //Constructor
    public SeckillExecution(long seckillId, SeckillStateEnum stateEnum,  org.seckill.entity.successKilled successKilled) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.successKilled = successKilled;
    }
    //失败
    public SeckillExecution(long seckillId, SeckillStateEnum stateEnum) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    //Getter and Setter
    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String sateInfo) {
        this.stateInfo = sateInfo;
    }

    public org.seckill.entity.successKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(org.seckill.entity.successKilled successKilled) {
        this.successKilled = successKilled;
    }

    @Override
    public String toString() {
        return "SeckillExecution{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", sateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }
}
