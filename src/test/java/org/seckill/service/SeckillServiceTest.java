package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
//service依赖于dao的配置
public class SeckillServiceTest {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;
    @Test
    public void getSeckillList() {
        List<seckill> list =seckillService.getSeckillList();
        logger.info("list={}",list);
        /**
         * Closing non transactional SqlSession --不是在事务控制下
         * list=[seckill{seckillId=1000, name='99元秒杀T恤福袋', number=100, startTIme=Sat Aug 15 08:00:00 CST 2020, endTime=Sun Aug 16 08:00:00 CST 2020, createTime=Tue Apr 19 04:46:59 CST 2022}, seckill{seckillId=1001, name='198元秒杀22cm冰墩墩', number=50, startTIme=Sat Aug 15 08:00:00 CST 2020, endTime=Sun Aug 16 08:00:00 CST 2020, createTime=Tue Apr 19 04:46:59 CST 2022}, seckill{seckillId=1002, name='4999元秒杀iPhone X', number=10, startTIme=Sat Aug 15 08:00:00 CST 2020, endTime=Sun Aug 16 08:00:00 CST 2020, createTime=Tue Apr 19 04:46:59 CST 2022}, seckill{seckillId=1003, name='8000元秒杀尼康z镜头组', number=1000, startTIme=Sat Aug 15 08:00:00 CST 2020, endTime=Sun Aug 16 08:00:00 CST 2020, createTime=Tue Apr 19 04:46:59 CST 2022}]
         */
    }

    @Test
    public void getById() {
        long id=1000;
        seckill seckill=seckillService.getById(id);
        logger.info("seckill={}",seckill);
        //测试结果：seckill=seckill{seckillId=1000, name='99元秒杀T恤福袋', number=100, startTIme=Sat Aug 15 08:00:00 CST 2020, endTime=Sun Aug 16 08:00:00 CST 2020, createTime=Tue Apr 19 04:46:59 CST 2022}
    }

    @Test
    public void exportSeckillUrl() {
        long id=1001;
        Exposer exposer=seckillService.exportSeckillUrl(id);
        //给exposer加一个toString方法方便打印
        logger.info("exposer={}",exposer);
        //运行结果：exposer=Exposer{exposed=false, md5='null', seckillId=1000, now=1650625817031, start=1597449600000, end=1597536000000}
        //修改秒杀开启时间后：exposer=Exposer{exposed=true, md5='748ce9796964bd49b496095fccd88d31', seckillId=1000, now=0, start=0, end=0}
    }

    @Test
    public void executeSeckill() {
        long id=1000,userPhone=13544687520L;
        String md5="748ce9796964bd49b496095fccd88d31";
        SeckillExecution seckillExecution=seckillService.executeSeckill(id,userPhone,md5);
        logger.info("SeckillExecution={}",seckillExecution);
        //报错：org.seckill.exception.SeckillException: SECKILL DATA REWRITE
        //修改：【!】md5.equals(getMD5(seckillId)
        /**
         * 19:19:55.602 [main] DEBUG o.s.j.d.DataSourceTransactionManager - Initiating transaction commit--事务提交
         * 19:19:55.602 [main] DEBUG o.s.j.d.DataSourceTransactionManager - Committing JDBC transaction on Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@bac7bc [wrapping: com.mysql.jdbc.JDBC4Connection@1798ca7]]
         * 19:19:55.604 [main] DEBUG o.s.j.d.DataSourceTransactionManager - Releasing JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@bac7bc [wrapping: com.mysql.jdbc.JDBC4Connection@1798ca7]] after transaction
         * 19:19:55.605 [main] INFO  o.seckill.service.SeckillServiceTest - SeckillExecution=org.seckill.dto.SeckillExecution@cbeb64
         */
    }
    @Test
    public void seckillLogic(){
        //集成以上两个,测试完整执行逻辑
        long id=1000;
        Exposer exposer=seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){//如果秒杀开启
            logger.info("exposer={}",exposer);
            long userPhone=13502145622L;
            String md5=exposer.getMd5();
            try {
                SeckillExecution seckillExecution=seckillService.executeSeckill(id,userPhone,md5);
                logger.info("SeckillExecution={}",seckillExecution);
            }catch (RepeatKillException e){//测试可重复执行
                logger.error(e.getMessage());
            }catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }
        }else{//秒杀未开启
            logger.warn("exposer={}",exposer);
        }
        //WARN  o.seckill.service.SeckillServiceTest - exposer=Exposer{exposed=false, md5='null', seckillId=1000, now=1650627057718, start=1650655195000, end=1651363200000}
        //开启时间突然变成当前时间，再次修改： SeckillExecution=SeckillExecution{seckillId=1000, state=1, sateInfo='秒杀成功', successKilled=successKilled{seckillId=1000, userPhone=13502145622, state=0, createTime=Sat Apr 23 03:33:35 CST 2022}}
    }
}