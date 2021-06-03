package com.plunger.service.impl;

import com.plunger.api.CommonResult;
import com.plunger.config.excel.ExcelProperties;
import com.plunger.constant.Constant;
import com.plunger.service.ExcelService;
import com.plunger.util.ExcelUtil;
import com.plunger.util.FileUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service("excelService")
public class ExcelServiceImpl implements ExcelService {

    @Resource
    ExcelProperties excelProperties;

    private static final Logger logger = LoggerFactory.getLogger(ExcelServiceImpl.class);

    @Override
    public CommonResult upload(MultipartFile uploadFile) {

        if (uploadFile.isEmpty() || uploadFile.getSize() == 0) {
            return CommonResult.failed("文件为空");
        }

        try {
            String filename = uploadFile.getOriginalFilename();
            String suffix = filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1) : "";
            String saveFileName = Instant.now().toEpochMilli() + (suffix.length() == 0 ? "" : "." + suffix);
            String filePath = FileUtil.getUploadPath() + saveFileName;
            String realFilePath = FileUtil.getAbsolutePath(filePath);

            File realFile = new File(realFilePath);
            if (!realFile.getParentFile().exists()) {
                realFile.getParentFile().mkdirs();
            }
            uploadFile.transferTo(realFile);
            JSONObject returnObj = new JSONObject();
            returnObj.put("saveFileName", saveFileName);
            logger.info("上传成功，文件保存在[" + realFilePath + "]");
            return CommonResult.success(returnObj, "上传成功，开始转化...");
        } catch (IOException e) {
            e.printStackTrace();
            return CommonResult.failed("文件上传异常");
        }
    }

    @Override
    public CommonResult resolve(JSONObject paramObj) {
        try {
            String saveFileName = paramObj.optString("saveFileName");
            String saveFilePath = FileUtil.getUploadPath() + saveFileName;
            String saveRealFilePath = FileUtil.getAbsolutePath(saveFilePath);
            File file = new File(saveRealFilePath);
            if (file == null) {
                return CommonResult.failed("未找到文件[" + saveFileName + "]");
            }
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
            XSSFSheet dataSheet = workbook.getSheet(Constant.EXCEL.DATA.SHEETNAME);
            int count = 0;
            for (int i = dataSheet.getLastRowNum() + 1; i >= 25; i--) {
                String wellName = ExcelUtil.getCellValue(dataSheet, "C" + i);
                if (!StringUtils.isEmpty(wellName)) {
                    String indexName = ExcelUtil.getCellValue(dataSheet, "A" + i);
                    if (!StringUtils.isEmpty(indexName)) {
                        count = new Integer(indexName);
                        break;
                    }
                }
            }

            if (count == 0) {
                return CommonResult.failed("未在[统计资料]sheet页找到有效组序");
            } else {
                logger.info("获取到" + count + "组数据，开始分析数据");
            }

            XSSFFormulaEvaluator evaluator;
            excelProperties.getBasicProperties().getSheetName();
            XSSFSheet changeSheet = workbook.getSheet(Constant.EXCEL.BASIC.SHEETNAME);
            XSSFCell printCell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.BASIC.PRINTCELLADDR);
            XSSFCell yuanCell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.BASIC.YUANCELLADDR);
            XSSFCell fangCell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.BASIC.FANGCELLADDR);
            XSSFCell jinCell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.BASIC.JINCELLADDR);
            String[] sheetNameYuanArr = Constant.EXCEL.YUAN.SHEETNAMES.split(",");
            String[] sheetNameFangArr = Constant.EXCEL.FANG.SHEETNAMES.split(",");
            String[] sheetNameJinArr = Constant.EXCEL.JIN.SHEETNAMES.split(",");
            List<String> resultSheetNameList = new ArrayList<>();

            for (int i = 1; i <= count; i++) {
                printCell.setCellValue(i);
                evaluator = new XSSFFormulaEvaluator(workbook);
                evaluator.evaluateAll();
                int yuanCount = new Double(yuanCell.getNumericCellValue()).intValue();
                int fangCount = new Double(fangCell.getNumericCellValue()).intValue();
                int jinCount = new Double(jinCell.getNumericCellValue()).intValue();
                logger.info("打印页码=" + i + "，圆=" + yuanCount + "，方=" + fangCount + "，井=" + jinCount);

                if (yuanCount > 0) {
                    for (String sheetName : sheetNameYuanArr) {
                        XSSFSheet oldSheet = workbook.getSheet(sheetName);
                        String newSheetName = "组" + i + sheetName;
                        ExcelUtil.cloneSheet(oldSheet, newSheetName);
                        resultSheetNameList.add(newSheetName);
                    }
                }

                if (fangCount > 0) {
                    for (String sheetName : sheetNameFangArr) {
                        XSSFSheet oldSheet = workbook.getSheet(sheetName);
                        String newSheetName = "组" + i + sheetName;
                        ExcelUtil.cloneSheet(oldSheet, newSheetName);
                        resultSheetNameList.add(newSheetName);
                    }
                }

                if (jinCount > 0) {
                    for (String sheetName : sheetNameJinArr) {
                        XSSFSheet oldSheet = workbook.getSheet(sheetName);
                        String newSheetName = "组" + i + sheetName;
                        ExcelUtil.cloneSheet(oldSheet, newSheetName);
                        resultSheetNameList.add(newSheetName);
                    }
                }
            }

            int totalSheetNum = workbook.getNumberOfSheets();
            for (int i = 0; i < totalSheetNum - resultSheetNameList.size(); i++) {
                workbook.removeSheetAt(0);
            }


            workbook.setActiveSheet(0);
            workbook.getSheetAt(0).showInPane(0, 0);
            String filePath = FileUtil.getResultFilePath() + file.getName();
            String realFilePath = FileUtil.getAbsolutePath(filePath);
            File realFile = new File(realFilePath);
            if (!realFile.getParentFile().exists()) {
                realFile.getParentFile().mkdirs();
            }
            FileOutputStream out = new FileOutputStream(realFilePath);
            workbook.write(out);
            out.close();
            workbook.close();
            JSONObject resultObj = new JSONObject();
            resultObj.put("resultFileName", file.getName());
            logger.info("转化成功，结果保存在[" + realFilePath + "]");
            return CommonResult.success(resultObj, "转化成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("运行出错", e);
            return CommonResult.failed("运行出错，请联系管理员");
        }
    }


}
