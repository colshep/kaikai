package com.plunger.controller;

import com.plunger.api.CommonResult;
import com.plunger.service.ExcelService;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/excel")
public class ExcelController {

    @Resource
    private ExcelService excelService;

    @RequestMapping("/modal")
    public ModelAndView uploadPage() {
        ModelAndView mav = new ModelAndView("/excel/uploadPage");
        return mav;
    }

    @RequestMapping("/upload")
    @ResponseBody
    public CommonResult upload(MultipartFile uploadFile) {
        return excelService.upload(uploadFile);
    }

}
