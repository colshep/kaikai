package com.plunger.service.impl;

import com.plunger.api.CommonResult;
import com.plunger.service.ExcelService;
import com.plunger.util.ExcelUtil;
import com.plunger.util.FileUtil;
import org.apache.poi.ss.formula.eval.NotImplementedFunctionException;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;

@Service("excelService")
public class ExcelServiceImpl implements ExcelService {

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

            XSSFSheet sheet1 = workbook.getSheet("挖隐");
            XSSFSheet sheet2 = workbook.getSheet("井石垫隐");
            XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(workbook);

            XSSFCell cellFW9 = ExcelUtil.getCell(sheet1, "FW9");
            XSSFCell cellGD9 = ExcelUtil.getCell(sheet1, "GD9");
            XSSFCell cellGE9 = ExcelUtil.getCell(sheet1, "GE9");
            XSSFCell cellGF9 = ExcelUtil.getCell(sheet1, "GF9");
            XSSFCell cellM27 = ExcelUtil.getCell(sheet2, "M27");

            cellFW9.setCellValue(1);
            evaluator = new XSSFFormulaEvaluator(workbook);
            System.out.println("打印页码=" + cellFW9.getRawValue());
            System.out.println("圆=" + evaluator.evaluate(cellGD9).getNumberValue());
            System.out.println("方=" + evaluator.evaluate(cellGE9).getNumberValue());
            System.out.println("无井=" + evaluator.evaluate(cellGF9).getNumberValue());
            System.out.println("高程=" + evaluator.evaluate(cellM27).getNumberValue());

            cellFW9.setCellValue(2);

            try {
                evaluator.evaluateAll();
            } catch (NotImplementedFunctionException e) {
                if (e.getFunctionName().equals("DISPIMG")) {

                }
            }

            System.out.println("打印页码=" + cellFW9.getRawValue());
            System.out.println("圆=" + evaluator.evaluate(cellGD9).getNumberValue());
            System.out.println("方=" + evaluator.evaluate(cellGE9).getNumberValue());
            System.out.println("无井=" + evaluator.evaluate(cellGF9).getNumberValue());
            System.out.println("高程=" + evaluator.evaluate(cellM27).getNumberValue());

//            XSSFSheet sheet = null;
//            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {//获取每个Sheet表
//                sheet = workbook.getSheetAt(i);
//
//                if ("挖隐".equals(sheet.getSheetName())) {
//                    XSSFCell cell = ExcelUtil.getCell(sheet, "FW9");
//                    System.out.println("打印页码=" + cell.getRawValue());
//                    System.out.println("圆=" + ExcelUtil.getCell(sheet, "GD9").getRawValue());
//                    System.out.println("方=" + ExcelUtil.getCell(sheet, "GE9").getRawValue());
//                    System.out.println("无井=" + ExcelUtil.getCell(sheet, "GF9").getRawValue());
//
//
//                    cell.setCellValue(1);
//                    XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(cell.getSheet().getWorkbook());
//                    evaluator.evaluate(cell);
//                    CellValue v = evaluator.evaluate(ExcelUtil.getCell(sheet, "GD9"));
//                    System.out.println("打印页码=" + cell.getRawValue());
//                    System.out.println("圆=" + v);
//                    System.out.println("方=" + ExcelUtil.getCell(sheet, "GE9").getRawValue());
//                    System.out.println("无井=" + ExcelUtil.getCell(sheet, "GF9").getRawValue());
//                }
//
//
//                if ("井石垫隐".equals(sheet.getSheetName())) {
//
//                }
//
//                for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {//获取每行
//                    XSSFRow row = sheet.getRow(j);
//                    for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {//获取每个单元格
//                        XSSFCell cell = row.getCell(k);
//                        System.out.print(row.getCell(k) + "\t");
//                    }
//                    System.out.println("---Sheet表" + i + "处理完毕---");
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommonResult.success();
    }

    public static void main(String[] args) {
        ExcelServiceImpl service = new ExcelServiceImpl();
        service.resolve(null);
    }
}
