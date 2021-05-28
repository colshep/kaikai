package com.plunger.service;

import com.plunger.api.CommonResult;
import net.sf.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {

    CommonResult upload(MultipartFile uploadFile);

}
