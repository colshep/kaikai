package com.plunger.service.impl;

import com.plunger.api.CommonResult;
import com.plunger.aspect.LogAspect;
import com.plunger.service.ExcelService;
import com.plunger.util.ExcelUtil;
import com.plunger.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.eval.NotImplementedFunctionException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
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
import java.util.ArrayList;
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
            resolve(realFile);
        } catch (IOException e) {
            e.printStackTrace();
            return CommonResult.failed("文件上传异常");
        }

        return CommonResult.success();
    }

    public CommonResult resolve(File file) {
        try {
            if (file == null) {
                file = new File("D:\\OneDrive\\Work\\kaikai\\templete.xlsx");
            }
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
            XSSFSheet dataSheet = workbook.getSheet("统计资料");
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
            XSSFSheet changeSheet = workbook.getSheet("挖隐");
            XSSFCell printCell = ExcelUtil.getCell(changeSheet, "FW9");
            XSSFCell yuanCell = ExcelUtil.getCell(changeSheet, "GD9");
            XSSFCell fangCell = ExcelUtil.getCell(changeSheet, "GE9");
            XSSFCell jinCell = ExcelUtil.getCell(changeSheet, "GF9");
            String[] sheetNameYuanArr = new String[]{"井素砼垫隐", "井素砼垫隐 (2)", "井基筋安隐", "井基筋安隐 (2)"};
            String[] sheetNameFangArr = new String[]{"井石垫隐", "井石垫隐 (2)"};
            String[] sheetNameJinArr = new String[]{"井基砼隐", "井基砼隐 (2)"};
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
            FileOutputStream out = new FileOutputStream("D:\\OneDrive\\Work\\kaikai\\result.xlsx");
            workbook.write(out);
            out.close();
            workbook.close();

            logger.info("复制成功，请查看结果");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("运行出错", e);
            return CommonResult.failed("运行出错，请联系管理员");
        }
        return CommonResult.success();
    }

    public static void main(String[] args) {
        ExcelServiceImpl service = new ExcelServiceImpl();
        service.resolve(null);
    }
}
