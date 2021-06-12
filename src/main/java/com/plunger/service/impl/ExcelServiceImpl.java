package com.plunger.service.impl;

import com.plunger.api.CommonResult;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("excelService")
public class ExcelServiceImpl implements ExcelService {

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
                return CommonResult.failed("未在[" + Constant.EXCEL.DATA.SHEETNAME + "]sheet页找到有效组序");
            } else {
                logger.info("获取到" + count + "组数据，开始分析数据");
            }

            XSSFFormulaEvaluator evaluator;
            XSSFSheet changeSheet = workbook.getSheet(Constant.EXCEL.BASIC.SHEETNAME);
            XSSFCell printCell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.BASIC.PRINTCELLADDR);
            XSSFCell yuanCell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.BASIC.YUANCELLADDR);
            XSSFCell fangCell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.BASIC.FANGCELLADDR);
            XSSFCell wujinCell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.BASIC.WUJINCELLADDR);
            XSSFCell baoguanCell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.BASIC.BAOGUANCELLADDR);
            XSSFCell shuizhunCell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.BASIC.SHUIZHUNCELLADDR);
            XSSFCell c15Cell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.BASIC.C15CELLADDR);
            XSSFCell c30Cell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.BASIC.C30CELLADDR);
            String[] sheetNameWuTiaoJianArr = Constant.EXCEL.WUTIAOJIAN.SHEETNAMES.split(",");
            String[] sheetNameYuanArr = Constant.EXCEL.YUAN.SHEETNAMES.split(",");
            String[] sheetNameFangArr = Constant.EXCEL.FANG.SHEETNAMES.split(",");
            String[] sheetNameWuJinArr = Constant.EXCEL.WUJIN.SHEETNAMES.split(",");
            String[] sheetNameBaoGuanArr = Constant.EXCEL.BAOGUAN.SHEETNAMES.split(",");
            String[] sheetNameShuizhunArr = Constant.EXCEL.SHUIZHUN.SHEETNAMES.split(",");
            String[] sheetNameC15Arr = Constant.EXCEL.C15.SHEETNAMES.split(",");
            String[] sheetNameC30Arr = Constant.EXCEL.C30.SHEETNAMES.split(",");
            int originalSheetNum = workbook.getNumberOfSheets();

            for (int i = 1; i <= count; i++) {
                printCell.setCellValue(i);
                evaluator = new XSSFFormulaEvaluator(workbook);
//                evaluator.evaluateInCell(printCell);
                evaluator.evaluateAll();
                int yuanCount = new Double(yuanCell.getNumericCellValue()).intValue();
                int fangCount = new Double(fangCell.getNumericCellValue()).intValue();
                int wujinCount = new Double(wujinCell.getNumericCellValue()).intValue();
                String baoguanFlag = baoguanCell.getStringCellValue();
                int shuizhunCount = new Double(shuizhunCell.getNumericCellValue()).intValue();
                String c15Flag = c15Cell.getStringCellValue();
                String c30Flag = c30Cell.getStringCellValue();
                logger.info("打印页码=" + i + "，圆=" + yuanCount + "，方=" + fangCount + "，无井=" + wujinCount +
                        "，包管=" + baoguanFlag + "，水准=" + shuizhunCount + "，C15=" + c15Flag + "，C30=" + c30Flag);
                List<Integer> resultSheetIndexList = new ArrayList<>();

                for (String sheetName : sheetNameWuTiaoJianArr) {
                    int index = workbook.getSheetIndex(sheetName);
                    if (index == -1) {
                        return CommonResult.failed("无法在[sheetNameWuTiaoJianArr]找名为[" + sheetName + "]的sheet页,请检查配置");
                    }
                    resultSheetIndexList.add(index);
                }

                if (yuanCount > 0) {
                    for (String sheetName : sheetNameYuanArr) {
                        int index = workbook.getSheetIndex(sheetName);
                        if (index == -1) {
                            return CommonResult.failed("无法在[sheetNameYuanArr]找名为[" + sheetName + "]的sheet页,请检查配置");
                        }
                        resultSheetIndexList.add(index);
                    }
                }

                if (fangCount > 0) {
                    for (String sheetName : sheetNameFangArr) {
                        int index = workbook.getSheetIndex(sheetName);
                        if (index == -1) {
                            return CommonResult.failed("无法在[sheetNameFangArr]找名为[" + sheetName + "]的sheet页,请检查配置");
                        }
                        resultSheetIndexList.add(index);
                    }
                }

                if (wujinCount > 0) {
                    for (String sheetName : sheetNameWuJinArr) {
                        int index = workbook.getSheetIndex(sheetName);
                        if (index == -1) {
                            return CommonResult.failed("无法在[sheetNameWuJinArr]找名为[" + sheetName + "]的sheet页,请检查配置");
                        }
                        resultSheetIndexList.add(index);
                    }
                }

                if ("√".equals(baoguanFlag)) {
                    for (String sheetName : sheetNameBaoGuanArr) {
                        int index = workbook.getSheetIndex(sheetName);
                        if (index == -1) {
                            return CommonResult.failed("无法在[sheetNameBaoGuanArr]找名为[" + sheetName + "]的sheet页,请检查配置");
                        }
                        resultSheetIndexList.add(index);
                    }
                }

                if (shuizhunCount > 8) {
                    for (String sheetName : sheetNameShuizhunArr) {
                        int index = workbook.getSheetIndex(sheetName);
                        if (index == -1) {
                            return CommonResult.failed("无法在[sheetNameShuizhunArr]找名为[" + sheetName + "]的sheet页,请检查配置");
                        }
                        resultSheetIndexList.add(index);
                    }
                }

                if ("√".equals(c15Flag)) {
                    for (String sheetName : sheetNameC15Arr) {
                        int index = workbook.getSheetIndex(sheetName);
                        if (index == -1) {
                            return CommonResult.failed("无法在[sheetNameC15Arr]找名为[" + sheetName + "]的sheet页,请检查配置");
                        }
                        resultSheetIndexList.add(index);
                    }
                }

                if ("√".equals(c30Flag)) {
                    for (String sheetName : sheetNameC30Arr) {
                        int index = workbook.getSheetIndex(sheetName);
                        if (index == -1) {
                            return CommonResult.failed("无法在[sheetNameC30Arr]找名为[" + sheetName + "]的sheet页,请检查配置");
                        }
                        resultSheetIndexList.add(index);
                    }
                }

                // 按照index对resultSheetIndexList进行排序
                Collections.sort(resultSheetIndexList);
                for (int i1 = 0; i1 < resultSheetIndexList.size(); i1++) {
                    int index = resultSheetIndexList.get(i1);
                    XSSFSheet oldSheet = workbook.getSheetAt(index);
                    String newSheetName = "组" + i + oldSheet.getSheetName();
                    ExcelUtil.cloneSheet(oldSheet, newSheetName);
                }
            }

            // 删除原来的sheet页
            for (int i = 0; i < originalSheetNum; i++) {
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
            return CommonResult.success(resultObj, "成功转化[" + count +"]组数据");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("运行出错", e);
            return CommonResult.failed("运行出错，请联系管理员。" + e.getMessage());
        }
    }


}
