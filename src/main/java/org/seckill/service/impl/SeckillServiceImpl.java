package org.seckill.service.impl;

import org.seckill.dao.seckillDao;
import org.seckill.dao.successKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.seckill;
import org.seckill.entity.successKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {
    //org.slf4j.LoggerFactory,统一的日志API
    private Logger logger= LoggerFactory.getLogger(this.getClass());

    //注入Service依赖
    @Autowired
    private seckillDao seckillDao;
    @Autowired
    private successKilledDao successKilledDao;

    //md5盐值字符串，用于混淆md5
    private final String slat="23H•᷄ࡇ•᷅ZY9UKaq*@））￥8BFaR";
    //code-generate-implementsMethod
    public List<seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    public seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        //优化点：缓存优化

        seckill seckill=seckillDao.queryById(seckillId);
        if(seckill==null) return new Exposer(false,seckillId);
        Date startTime=seckill.getStartTime();
        Date endTime=seckill.getEndTime();
        Date nowTime= new Date();
        if(nowTime.getTime()<startTime.getTime()||nowTime.getTime()>endTime.getTime())
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        //转化特定字符串的过程，不可逆
        String md5=getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    private String getMD5(long seckillId){
        String base=seckillId+"/"+slat;
        String md5= DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        //检查md5
        if(md5==null||!md5.equals(getMD5(seckillId))){
            throw new SeckillException("SECKILL DATA REWRITE");
        }

        //执行秒杀逻辑：减库存+记录购买行为

        try {
            Date nowTime=new Date();
            int updateCount=seckillDao.reduceNumber(seckillId,nowTime);
            if(updateCount<=0){
                //没有更新到记录，秒杀结束
                throw new SeckillCloseException("SECKILL IS CLOSED");
            }else {
                //成功，记录购买行为
                int insertCount= successKilledDao.insertSuccessKilled(seckillId,userPhone);
                if(insertCount<=0){
                    //重复秒杀
                    throw new RepeatKillException("SECKILL REPEATED");
                }else {
                    //秒杀成功
                    successKilled successKilled=successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
                }
            }
        }catch (SeckillCloseException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            //所有编译期异常转化为运行期异常
            throw new SeckillException("seckill inner error:"+e.getMessage());
        }//捕捉所有异常，一旦有错就回滚
    }
}
