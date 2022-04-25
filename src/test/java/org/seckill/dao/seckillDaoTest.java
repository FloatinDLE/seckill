package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class seckillDaoTest {
    //注入DAO实现类依赖
    @Resource
    private  seckillDao seckillDao;


    @Test
    public void testqueryById() throws Exception {
        long id=1000;
        seckill seckill= seckillDao.queryById(id);
        System.out.println((seckill.getName()));
        System.out.println(seckill);
        /**
         * Debug结果：
         * 99元秒杀T恤福袋
         * seckill{seckillId=1000, name='99元秒杀T恤福袋', number=100, startTIme=Sat Aug 15 08:00:00 CST 2020, endTime=Sun Aug 16 08:00:00 CST 2020, createTime=Tue Apr 19 04:46:59 CST 2022}
         */
    }

    @Test
    public void queryAll() {
        /**
         * Caused by: org.apache.ibatis.binding.BindingException:
         * Parameter 'offset' not found. Available parameters are [arg1, arg0, param1, param2]
         */
        //java没有保存形参的记录:queryAll(int offset,int limit) ->queryAll(arg0,arg1)
        //修改seckillDao.java:  List<seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
        List<seckill> seckills=seckillDao.queryAll(0,100);
        for(seckill seckill:seckills){
            System.out.println(seckill);
        }
        /**
         * 运行结果：
         * seckill{seckillId=1000, name='99元秒杀T恤福袋', number=100, startTIme=Sat Aug 15 08:00:00 CST 2020, endTime=Sun Aug 16 08:00:00 CST 2020, createTime=Tue Apr 19 04:46:59 CST 2022}
         * seckill{seckillId=1001, name='198元秒杀22cm冰墩墩', number=50, startTIme=Sat Aug 15 08:00:00 CST 2020, endTime=Sun Aug 16 08:00:00 CST 2020, createTime=Tue Apr 19 04:46:59 CST 2022}
         * seckill{seckillId=1002, name='4999元秒杀iPhone X', number=10, startTIme=Sat Aug 15 08:00:00 CST 2020, endTime=Sun Aug 16 08:00:00 CST 2020, createTime=Tue Apr 19 04:46:59 CST 2022}
         * seckill{seckillId=1003, name='8000元秒杀尼康z镜头组', number=1000, startTIme=Sat Aug 15 08:00:00 CST 2020, endTime=Sun Aug 16 08:00:00 CST 2020, createTime=Tue Apr 19 04:46:59 CST 2022}
         */
    }

    @Test
    public void reduceNumber() {
        //int reduceNumber(@Param("seckillId") long seckillId,@Param("killTime") Date killTime);
        Date killTime=new Date();
        int updateCount=seckillDao.reduceNumber(1000L,killTime);
        System.out.println("更新时间:"+updateCount);
        /**
         * 21:26:15.542 [main] DEBUG org.mybatis.spring.transaction.SpringManagedTransaction - JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@f6d34c [wrapping: com.mysql.jdbc.JDBC4Connection@271dd6]]
         * will not be managed by Spring ——没有被spring托管
         * 21:26:15.549 [main] DEBUG org.seckill.dao.seckillDao.reduceNumber - ==>  Preparing:
         * update seckill
         * set number =number -1
         * where seckill_id =? and start_time <= ? and end_time >= ? and number >0; ——我们的SQL语句
         * 21:26:15.572 [main] DEBUG org.seckill.dao.seckillDao.reduceNumber - ==> Parameters: 1000(Long), 2022-04-18 21:26:15.334(Timestamp), 2022-04-18 21:26:15.334(Timestamp)
         * 21:26:15.577 [main] DEBUG org.seckill.dao.seckillDao.reduceNumber - <==    Updates: 0 ——时间不对
         * 更新时间:0
         */
    }
}