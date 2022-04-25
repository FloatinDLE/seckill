//存放主要交互逻辑js代码
//JavaScript 模块化
var seckill={
    //封装秒杀相关ajax的url,方便维护修改，不用到交互代码中找
    URL : {
        now:function () {
            return '/seckill/time/now';
        },
        exposer :function (seckillId) {
            return '/seckill/'+seckillId+'/exposer';
        },
        execution :function (seckillId,md5) {
            return '/seckill/'+seckillId+'/'+md5+'/execution';
        }
    },
    handleSeckillkill: function(seckillId,node){
        //获取秒杀地址，控制显示逻辑，执行秒杀
        // 秒杀按钮，开始时隐藏
        node.hide()
            .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId),{},function (result) {
            //在回调函数中执行交互流程
            if(result && result['success']){
                var exposer =result['data'];
                if(exposer['exposed']){
                    //已经开启秒杀
                    //获取秒杀地址
                    var md5=exposer['md5'];
                    var killUrl=seckill.URL.execution(seckillId,md5);
                    console.log("killURL:"+killUrl);
                    //绑定一次点击事件，防止用户多次点击造成服务器压力
                    $('#killBtn').one('click',function () {
                        //执行秒杀请求
                        //1.先禁用按钮
                        $(this).addClass('disabled');
                        //2.发送秒杀请求
                        $.post(killUrl,{},function (result) {
                            if(result && result['success']){
                                var killResult=result['data'];
                                var state=killResult['state'];
                                var stateInfo=killResult['stateInfo'];
                                console.log("stateInfo:"+stateInfo);
                                //3.显示秒杀结果
                                node.html('<span class="label label-success">'+stateInfo+'</span>')
                            }
                        });
                    });
                    node.show();
                }else {
                    //未开启秒杀，说明用户电脑时间快了点,重新进入计时
                    var now=exposer['now'];
                    var start=exposer['start'];
                    var end =exposer['end'];
                    seckill.countdown(seckillId,now,start,end);
                }
            }else{
                console.log('result:'+result);
            }
        });
    },
    //验证手机号。多个地方都用得到，所以放上面
    validatePhone : function(phone){
        if(phone && phone.length==11 &&!isNaN(phone)){
            return true;
        }else{
            return false;
        }
    },
    //开始倒计时
    countdown : function(seckillId,nowTime,startTime,endTime){
        var seckillBox=$('#seckill-box')
        //时间判断
        if(nowTime>endTime){
            //秒杀结束
            seckillBox.html('秒杀已结束！');
        }else if(nowTime<startTime){
            //秒杀未开始,倒计时
            var killTime=new Date(startTime+1000);//加1秒防止用户时间偏移
            //每一次时间变化都回调函数
            seckillBox.countdown(killTime,function (event) {
                //控制时间格式，输出到span标签
                var format=event.strftime('秒杀倒计时： %D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown',function () {
                //倒计时结束后回调事件，秒杀开始
                seckill.handleSeckillkill(seckillId,seckillBox);
            });
        }else{
            //秒杀开始
            seckill.handleSeckillkill(seckillId,seckillBox);
        }
    },

    //详情页秒杀逻辑
    //相当于seckill.detail.init(params);
    detail: {
        //详情页初始化
        init :function (params) {
            //手机验证和登录，计时交互
            //规划交互流程

            //1.验证
            //在cookie查找手机号
            var killPhone=$.cookie('killPhone');
            //拿到detail.jsp从params中传来的参数
            var id=params['seckillId'];
            var startTime=params['startTime'];
            var endTime=params['endTime'];
            if(!seckill.validatePhone(killPhone)){
                //killPhone为空，需要绑定手机号
                var killPhoneModal =$('#killPhoneModal');
                //显示弹出层
                killPhoneModal.modal({
                    show:true,//显示弹出层，默认fade隐藏
                    //不准关闭弹出层
                    backdrop:'static',//禁止位置关闭
                    keyboard: false //关闭键盘事件
                });
                //绑定按钮事件
                $('#killPhoneBtn').click(function () {
                    var inputPhone=$('#killPhoneKey').val();
                    if (seckill.validatePhone(inputPhone)){
                        //电话写入cookie，有效期为7天，只在/seckill下有效
                        $.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'})
                        //刷新页面
                        window.location.reload();
                    }else{
                        //用户输入的手机号有问题,先隐藏，放内容，过300毫秒显示
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">无效手机号！请输入11位纯数字。</label>').show(300);
                    }
                });
            }
            //已经登录

            //2.计时交互，未开始显示倒计时
            $.get(seckill.URL.now(),{},function (result) {
                if(result && result['success']){
                    var nowTime=result['data'];
                    //时间判断
                    seckill.countdown(id,nowTime,startTime,endTime);
                }else{
                    console.log('result:'+result);
                }
            })

        }
    }
}