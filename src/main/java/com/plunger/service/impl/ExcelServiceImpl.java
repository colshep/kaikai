package com.plunger.service.impl;

import com.plunger.api.CommonResult;
import com.plunger.constant.Constant;
import com.plunger.service.ExcelService;
import com.plunger.util.ExcelUtil;
import com.plunger.util.FileUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

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

            // 获取组数
            XSSFSheet dataSheet = workbook.getSheet(Constant.EXCEL.DATA_SHEET_NAME);
            int count = 0;
            for (int i = dataSheet.getLastRowNum() + 1; i >= 25; i--) {
                // step1.获取C列最后一个井号
                String wellName = ExcelUtil.getCellValue(dataSheet, "C" + i);
                if (!StringUtils.isEmpty(wellName)) {
                    // step2.获取A列最后一个组序
                    String indexName = ExcelUtil.getCellValue(dataSheet, "A" + i);
                    if (!StringUtils.isEmpty(indexName)) {
                        count = new Integer(indexName);
                        break;
                    }
                }
            }
            if (count == 0) {
                return CommonResult.failed("未在[" + dataSheet.getSheetName() + "]sheet页找到有效组序");
            } else {
                logger.info("获取到" + count + "组数据，开始分析数据");
            }

            // 获取打印页码单元格
            XSSFFormulaEvaluator evaluator;
            XSSFSheet changeSheet = workbook.getSheet(Constant.EXCEL.CONFIG_SHEET_NAME);
            XSSFCell changeCell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.CHANGE_CELL_ADDR);
            int originalSheetNum = workbook.getNumberOfSheets();

            // 捕获所有随机值公式
            logger.info("============================================捕获所有随机值公式");
            Map<String, String> randMap = new HashMap<>();
            List<String> randList = new ArrayList<>();
            for (int i = 0; i <= originalSheetNum - 1; i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                    XSSFRow row = sheet.getRow(j);
                    if (row == null) {
                        continue;
                    }
                    for (int k = row.getFirstCellNum(); k <= row.getLastCellNum(); k++) {
                        XSSFCell cell = row.getCell(k);
                        if (cell != null && CellType.FORMULA == cell.getCellType()) {
                            String formualStr = cell.getCellFormula();
                            if (formualStr.indexOf("RAND(") > -1 || formualStr.indexOf("RANDBETWEEN(") > -1) {
                                String key = sheet.getSheetName() + "-" + cell.getAddress().formatAsString();
                                randMap.put(key, formualStr);
                                randList.add(key);
                            }
                        }
                    }
                }
            }

            for (int i = 1; i <= count; i++) {
                logger.info("============================================开始处理页码" + i);

                // 设置当前打印组序
                XSSFCell printCell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.PRINT_CELL_ADDR);
                changeCell.setCellValue(i);
                evaluator = new XSSFFormulaEvaluator(workbook);

                // 按照先后顺序赋值所有随机值
                logger.info("============================================重新赋值随机值");
                for (String key : randList) {
                    String[] keyArr = key.split("-");
                    String sheetName = keyArr[0];
                    String R1C1Addr = keyArr[1];
                    XSSFCell cell = ExcelUtil.getCell(workbook, sheetName, R1C1Addr);
                    cell.setCellType(CellType.FORMULA);
                    cell.setCellFormula(randMap.get(key));
                    CellValue cellValue = evaluator.evaluate(cell);
                    cell.removeFormula();
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(cellValue.getNumberValue());
                }

                // 重新运算所有公式
                logger.info("============================================重新运算所有公式");
                evaluator.evaluateAll();

                // 筛选需要打印sheet页的index
                List<Integer> resultSheetIndexList = new ArrayList<>();
                while (printCell != null) {
                    XSSFRow row = printCell.getRow();
                    String printCellValue = ExcelUtil.getCellValue(printCell);
                    if ("1".equals(printCellValue)) {
                        XSSFCell sheetNamesCell = row.getCell(printCell.getColumnIndex() + 1);
                        String sheetNames = ExcelUtil.getCellValue(sheetNamesCell);
                        if (!StringUtils.isEmpty(sheetNames)) {
                            String[] sheetNamesArr = sheetNames.split(",");
                            for (String sheetName : sheetNamesArr) {
                                if (!StringUtils.isEmpty(sheetName)) {
                                    int sheetIndex = workbook.getSheetIndex(sheetName);
                                    if (sheetIndex == -1) {
                                        String errorMsg = "无法找到名为[" + sheetName + "]的sheet页,请检查配置";
                                        logger.info(errorMsg);
                                        return CommonResult.failed(errorMsg);
                                    } else {
                                        resultSheetIndexList.add(sheetIndex);
                                    }
                                }
                            }
                        }
                    } else if (StringUtils.isEmpty(printCellValue)) {
                        break;
                    }
                    XSSFRow nextRow = printCell.getSheet().getRow(row.getRowNum() + 1);
                    if (nextRow == null) {
                        break;
                    }
                    printCell = nextRow.getCell(printCell.getColumnIndex());
                }

                // 按照index对resultSheetIndexList进行排序
                Collections.sort(resultSheetIndexList);

                // 复制sheet页
                for (int i1 = 0; i1 < resultSheetIndexList.size(); i1++) {
                    int index = resultSheetIndexList.get(i1);
                    XSSFSheet oldSheet = workbook.getSheetAt(index);
                    String newSheetName = "组" + i + oldSheet.getSheetName();
                    logger.info("=========================正在处理" + newSheetName);
                    ExcelUtil.cloneSheet(oldSheet, newSheetName);
                }
                logger.info("============================================页码" + i + "处理结束");
            }

            // 删除原来的sheet页
            for (int i = 0; i < originalSheetNum; i++) {
                workbook.removeSheetAt(0);
            }

            // 按照各组(浇筑记录C15,浇筑记录C30),各组(旁站记录C15,旁站记录C30),各组挖准,各组其他sheet页的顺序对sheet页分类排序
            int sheetNum = workbook.getNumberOfSheets();
            List<String> sortedSheetNameList = new ArrayList<>();
            for (int i = 0; i < sheetNum; i++) {
                String sheetName = workbook.getSheetAt(i).getSheetName();
                if (sheetName.contains("浇筑记录")) {
                    sortedSheetNameList.add(sheetName);
                }
            }
            for (int i = 0; i < sheetNum; i++) {
                String sheetName = workbook.getSheetAt(i).getSheetName();
                if (sheetName.contains("旁站记录")) {
                    sortedSheetNameList.add(sheetName);
                }
            }
            for (int i = 0; i < sheetNum; i++) {
                String sheetName = workbook.getSheetAt(i).getSheetName();
                if (sheetName.contains("挖准")) {
                    sortedSheetNameList.add(sheetName);
                }
            }
            for (int i = 0; i < sortedSheetNameList.size(); i++) {
                String sheetName = sortedSheetNameList.get(i);
                workbook.setSheetOrder(sheetName, i);
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
            return CommonResult.success(resultObj, "成功转化[" + count + "]组数据");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("运行出错", e);
            return CommonResult.failed("运行出错，请联系管理员。" + e.getMessage());
        }
    }

    @Override
    public CommonResult resolve2(JSONObject paramObj) {
        try {
            String saveFileName = paramObj.optString("saveFileName");
            String saveFilePath = FileUtil.getUploadPath() + saveFileName;
            String saveRealFilePath = FileUtil.getAbsolutePath(saveFilePath);
            File file = new File(saveRealFilePath);
            if (file == null) {
                return CommonResult.failed("未找到文件[" + saveFileName + "]");
            }
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
            XSSFSheet sheet = workbook.getSheet("sheet1");
            XSSFSheet sheet2 = workbook.getSheet("sheet2");

            Map<String, String> randMap = new HashMap<>();

            for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                    XSSFCell cell = row.getCell(j);
                    // 把所有随机数赋值写死
                    if (cell != null && CellType.FORMULA == cell.getCellType()) {
                        String formualStr = cell.getCellFormula();
                        if (formualStr.indexOf("RAND(") == 0 || formualStr.indexOf("RANDBETWEEN(") == 0) {
                            randMap.put(sheet.getSheetName() + "-" + ExcelUtil.getR1C1Addr(cell), formualStr);
                            XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(cell.getSheet().getWorkbook());
                            CellValue cellValue = evaluator.evaluate(cell);
                            double value = cellValue.getNumberValue();
                            cell.removeFormula();
                            cell.setCellType(CellType.NUMERIC);
                            cell.setCellValue(value);
                            logger.info(cell.getAddress().formatAsString());
                        }
                    }
                }
            }

//            ExcelUtil.cloneSheet(sheet, "copyed sheet1");
//            ExcelUtil.cloneSheet(sheet2, "copyed sheet2");

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
            return CommonResult.success(resultObj, "成功转化组数据");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("运行出错", e);
            return CommonResult.failed("运行出错，请联系管理员。" + e.getMessage());
        }
    }


}
