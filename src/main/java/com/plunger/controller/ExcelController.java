package com.plunger.controller;

import com.plunger.api.CommonResult;
import com.plunger.service.ExcelService;
import com.plunger.util.FileUtil;
import net.sf.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.File;
import java.net.URLEncoder;

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

    @RequestMapping("/resolve")
    @ResponseBody
    public CommonResult resolve(@RequestBody JSONObject paramObj) {
        return excelService.resolve(paramObj);
    }

    @GetMapping("download/{saveFileName}")
    public ResponseEntity<byte[]> download(@PathVariable("saveFileName") String saveFileName, @RequestHeader("User-Agent") String userAgent) throws Exception {
        // 下载文件的路径
        String saveFilePath = FileUtil.getResultFilePath() + saveFileName;
        String saveRealFilePath = FileUtil.getAbsolutePath(saveFilePath);
        File file = new File(saveRealFilePath);
        // ok表示http请求中状态码200
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
        builder.contentLength(file.length());
        builder.contentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = URLEncoder.encode(file.getName(), "UTF-8");
        if (userAgent.indexOf("MSIE") > 0) {
            builder.header("Content-Disposition", "attachment; filename=" + filename);
        } else {
            builder.header("Content-Disposition", "attacher; filename*=UTF-8''" + filename);
        }
        return builder.body(FileCopyUtils.copyToByteArray(file));
    }

}
