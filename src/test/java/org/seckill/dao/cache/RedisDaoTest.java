package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.seckillDao;
import org.seckill.entity.seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {
    private long id =1001;
    @Autowired
    private  RedisDao redisDao;
    @Autowired
    private org.seckill.dao.seckillDao seckillDao;
    @Test
    public void testSeckill() {//测试前记得启动Redis
        seckill seckill =redisDao.getSeckill(id);
        if(seckill == null){
            seckill =seckillDao.queryById(id);
            if(seckill!=null){
                String result=redisDao.putSeckill(seckill);
                System.out.println(result);
                seckill=redisDao.getSeckill(id);
                System.out.println(seckill);
                /*OK
                seckill{seckillId=1001, name='198元秒杀22cm冰墩墩', number=50, startTIme=Sun May 15 08:00:00 CST 2022, endTime=Mon May 16 08:00:00 CST 2022, createTime=Tue Apr 19 04:46:59 CST 2022}*/
            }
        }
    }
}