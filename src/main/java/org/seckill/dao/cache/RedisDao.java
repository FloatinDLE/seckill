package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JedisPool jedisPool;
    //seckill.class类的字节码，通过反射拿到字节码的属性，RuntimeSchema基于class做一个模式，在创建对象时会根据模式来赋予相应的值
    private RuntimeSchema<seckill> schema =RuntimeSchema.createFrom(seckill.class);

    private RedisDao(String ip,int port){
        jedisPool =new JedisPool(ip,port);
    }

    //通过Redis拿到seckill对象
    public seckill getSeckill(long seckillId){
        //Redis操作逻辑
        try {
            Jedis jedis =jedisPool.getResource();
            try {
                String key="seckill:"+seckillId;
                //jedis没有实现内部序列化操作
                //get到的是一个二进制数组，需要通过反序列化拿到seckill类型Object
                //采用自定义序列化 Protostuff
                byte[] bytes=jedis.get(key.getBytes());
                if(bytes !=null){//从缓存中获取到
                    seckill seckill=schema.newMessage();//空对象
                    ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);//seckill被反序列化
                    return seckill;
                }
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }
    //发现缓存中没有时放入
    public String putSeckill(seckill seckill){
        //set: 把object序列化为字节数组
        try{
            Jedis jedis=jedisPool.getResource();
            try {
                String key="seckill:"+seckill.getSeckillId();
                //缓存器
                byte[] bytes=ProtostuffIOUtil.toByteArray(seckill,schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //超时缓存
                int timeout=60*60;//1小时
                String result=jedis.setex(key.getBytes(),timeout,bytes);
                return result;
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}
