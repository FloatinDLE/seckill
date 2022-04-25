package org.seckill.enums;

public enum SeckillStateEnum {
    //枚举常量，枚举类的实例
    SUCCESS(1,"秒杀成功"),
    END(0,"秒杀结束"),
    REPEAT_KILL(-1,"重复秒杀"),
    INNER_ERROR(-2,"系统异常"),
    DATA_REWRITE(-3,"数据篡改");

    //成员变量
    private int state;
    private String stateInfo;

    //构造方法
    SeckillStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }
    //getter

    public int getState() {
        return state;
    }
    public String getStateInfo() {
        return stateInfo;
    }
    //普通方法，遍历所有state找到当前传入的index的状态
    public static SeckillStateEnum stateOf(int index){
        for (SeckillStateEnum state:values()){
            if(state.getState()==index){
                return  state;
            }
        }
        return null;
    }
}
