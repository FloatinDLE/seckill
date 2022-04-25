package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")//模块--url:/模块/资源/{id}/细分/seckill/list
public class SeckillController {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)//不是get的HTTP请求全部被驳回
    public String list(Model model){
        //list.jsp(页面模板)+model(数据)=ModelAndView
        //获取列表页
        List<seckill> list=seckillService.getSeckillList();
        model.addAttribute("list",list);//model存放数据
        return "list"; // =/WEB-INF/jsp/"list".jsp
    }
    //详情页,{seckillId}--占位符
    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model){
        if(seckillId==null) return "redirect:/seckill/list";//重定向到列表页
        seckill seckill=seckillService.getById(seckillId);
        if(seckill==null) return "forward:/seckill/list"; //请求转发
        model.addAttribute("seckill",seckill);
        return "detail"; //符合要求就跳转到detail页
    }

    /**
     * ajax json
     * AJAX 就是异步 JavaScript 和 XML，
     * 遵循 AJAX 模型，Web 应用程序可以以异步的方式发送数据以及从服务器上检索数据，而不影响现有页面的显示行为。
     * 在客户端和服务器之间使用 JSON 传递 AJAX 更新
     */
    //暴露秒杀接口
    @RequestMapping(value = "/{seckillId}/exposer",
            method = {RequestMethod.GET,RequestMethod.POST},
            produces = {"application/json;charset=utf-8"})//表示功能处理方法将生产json格式的数据
    @ResponseBody //将返回的数据类型(这里是SeckillResult<Exposer>)封装为json
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        //在dto下新建SeckillResult类
        SeckillResult<Exposer> result;
        try{
            Exposer exposer=seckillService.exportSeckillUrl(seckillId);
            result=new SeckillResult<Exposer>(true,exposer);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            result=new SeckillResult<Exposer>(false,e.getMessage());
        }//ctrl+alt+t--自动生成代码块
        return result;
    }

    //执行秒杀
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = {RequestMethod.GET,RequestMethod.POST},
            produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone",required = false) Long phone){
        //require=false,不是必需，验证逻辑我们自己来处理
        if(phone==null){
            return new SeckillResult<SeckillExecution>(false,"未注册");
        }
        SeckillResult<SeckillExecution> result;
        try {
            SeckillExecution execution=seckillService.executeSeckill(seckillId,phone,md5);
            return new SeckillResult<SeckillExecution>(true,execution);
        }catch (RepeatKillException e){
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,execution);
        }
        catch (SeckillCloseException e){
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(true,execution);
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true,execution);
        }
    }

    //获取系统时间
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now=new Date();
        return new SeckillResult<Long>(true,now.getTime());
    }
}
