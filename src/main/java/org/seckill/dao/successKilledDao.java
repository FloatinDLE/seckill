package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.successKilled;

public interface successKilledDao {
    //记录用户购买明细，可过滤重复（联合主键）
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    //根据id查询当前秒杀成功对象并携带seckill实体
    successKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}
