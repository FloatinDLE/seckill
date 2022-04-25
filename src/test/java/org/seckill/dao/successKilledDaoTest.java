package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.seckill.entity.successKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class successKilledDaoTest {
    @Resource
    private  successKilledDao successKilledDao;

    //int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
    @Test
    public void insertSuccessKilled() {
        long id=1001L;
        long phone=13502154786L;
        int insertCount=successKilledDao.insertSuccessKilled(id,phone);
        System.out.println("insertCount="+insertCount);
        /**
         * 第一次运行：
         * insertCount=1
         * 第二次运行：
         * insertCount=0 （不允许重复插入，联合主键+ignore做到的）
         */
    }

    //successKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
    @Test
    public void queryByIdWithSeckill() {
        long id=1000L;
        long phone=13502154786L;
        successKilled successKilled=successKilledDao.queryByIdWithSeckill(id,phone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
        /**
         * successKilled{
         * seckillId=1000,
         * userPhone=13502154786,
         * state=-1, ——无效
         * createTime=Tue Apr 19 23:20:35 CST 2022
         * }
         * seckill{
         * seckillId=1000,
         * name='99元秒杀T恤福袋',
         * number=100,
         * startTIme=Sat Aug 15 08:00:00 CST 2020,
         * endTime=Sun Aug 16 08:00:00 CST 2020,
         * createTime=Tue Apr 19 04:46:59 CST 2022
         * }
         *
         * 修改successKilledDao.xml,插入时的state为0
         * <insert id="insertSuccessKilled">
         *     insert ignore into success_killed(seckill_id,user_phone,state)
         *     values (#{seckillId},#{userPhone},0)
         *</insert>
         *
         * id改为1001再次测试：
         *successKilled{seckillId=1001, userPhone=13502154786,
         * state=0,  ————成功
         * createTime=Tue Apr 19 23:49:54 CST 2022}
         */
    }
}