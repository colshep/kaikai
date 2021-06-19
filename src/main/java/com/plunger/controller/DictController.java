package com.plunger.controller;


import com.plunger.api.CommonResult;
import com.plunger.bean.plunger.Dict;
import com.plunger.constant.Constant;
import com.plunger.mapper.plunger.DictMapper;
import com.plunger.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/dict")
public class DictController {

    private static final Logger logger = LoggerFactory.getLogger(DictController.class);

    @Resource
    private DictMapper dictMapper;

    /**
     * 获取列表
     *
     * @return
     */
    @RequestMapping("/list")
    public ModelAndView usersList() {
        List<Dict> dictList = dictMapper.findAll();
        ModelAndView mav = new ModelAndView("/dict/dictListPage");
        mav.addObject("dictList", dictList);
        return mav;
    }


    /**
     * 获取详细信息
     *
     * @param unid
     * @return
     */
    @RequestMapping("/modal/{unid}")
    public ModelAndView modalPage(@PathVariable String unid) {
        Dict dict;
        String fn = "update";
        if ("insert".equals(unid)) {
            dict = new Dict();
            dict.setUnid(UUIDUtil.getUUID32());
            fn = "insert";
        } else {
            dict = dictMapper.findByUnid(unid);
        }
        ModelAndView mav = new ModelAndView("/dict/dictModalPage");
        mav.addObject("dict", dict);
        mav.addObject("fn", fn);
        return mav;
    }

    @RequestMapping("/insert")
    @ResponseBody
    public CommonResult insert(Dict dict) {
        // @RequestBody注解用来绑定通过http请求中application/json类型上传的数据
        // @RequestParam用来处理Content-Type: 为 application/x-www-form-urlencoded编码的内容
        dictMapper.insert(dict);
        return refreshCache();
    }

    @RequestMapping("/update")
    @ResponseBody
    public CommonResult update(Dict dict) {
        dictMapper.update(dict);
        return refreshCache();
    }

    @RequestMapping("/delete/{unid}")
    @ResponseBody
    public CommonResult delete(@PathVariable String unid) {
        dictMapper.deleteByUnid(unid);
        return refreshCache();
    }

    @RequestMapping("/refreshCache")
    @ResponseBody
    public CommonResult refreshCache() {
        try {
            Constant.refresh();
        } catch (Exception e) {
            logger.error("加载配置文件失败，请联系管理员");
            e.printStackTrace();
            return CommonResult.failed("加载配置文件失败，请联系管理员");
        }
        return CommonResult.success();
    }
}
