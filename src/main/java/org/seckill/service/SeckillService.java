package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

public interface SeckillService {
    //查询所有秒杀记录
    List<seckill> getSeckillList();
    //查询单个秒杀记录
    seckill getById(long seckillId);

    //秒杀开始时输出（暴露）秒杀接口地址，否则输出系统时间和秒杀时间
    //防止被恶意使用脚本秒杀
    Exposer exportSeckillUrl(long seckillId);

    //执行秒杀,md5验证用户URL是否被篡改
    //如果执行异常
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws SeckillException, RepeatKillException, SeckillCloseException;
}
