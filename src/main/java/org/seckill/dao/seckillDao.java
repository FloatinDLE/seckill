package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.seckill;

import java.util.Date;
import java.util.List;

public interface seckillDao {
    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return 表示更新的记录行数
     */
    int reduceNumber(@Param("seckillId") long seckillId,@Param("killTime") Date killTime);

    //根据id查询对象
    seckill queryById(long seckillId);
    //根据偏移量查询列表
    List<seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
}
