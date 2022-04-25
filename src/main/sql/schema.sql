CREATE DATABASE seckill;
use seckill;
CREATE TABLE seckill(
`seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
`name` varchar(120) NOT NULL COMMENT '商品名称',
`number` int NOT NULL COMMENT '库存数量',
`start_time` timestamp NOT NULL COMMENT '秒杀开启时间',
`end_time` timestamp NOT NULL COMMENT '秒杀结束时间',
`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',

PRIMARY KEY (seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

insert into
    seckill(name,number,start_time,end_time)
values
    ('99元秒杀T恤福袋',100,'2020-8-15 00:00:00','2020-8-16 00:00:00'),
    ('198元秒杀22cm冰墩墩',50,'2020-8-15 00:00:00','2020-8-16 00:00:00'),
    ('4999元秒杀iPhone X',10,'2020-8-15 00:00:00','2020-8-16 00:00:00'),
    ('8000元秒杀尼康z镜头组',1000,'2020-8-15 00:00:00','2020-8-16 00:00:00');

CREATE TABLE success_killed(
    `seckill_id` bigint NOT NULL COMMENT '商品库存id',
    `user_phone` bigint NOT NULL COMMENT '用户手机号',
    `state` tinyint NOT NULL  DEFAULT -1 COMMENT '状态，-1：无效，0：成功，1：已付款，2：已发货',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    PRIMARY KEY (seckill_id,user_phone),
    key idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';
