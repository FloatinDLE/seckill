package org.seckill.exception;

//秒杀相关业务异常（所有）（运行期异常）————还有编译期异常
public class SeckillException extends RuntimeException{
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
