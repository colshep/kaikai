package com.plunger.service.impl;

import com.plunger.api.CommonResult;
import com.plunger.constant.Constant;
import com.plunger.service.ExcelService;
import com.plunger.util.ExcelUtil;
import com.plunger.util.FileUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualPictureProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPictureNonVisual;
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
            XSSFSheet dataSheet = workbook.getSheet(Constant.EXCEL.DATA_SHEET_NAME);
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
                return CommonResult.failed("未在[" + dataSheet.getSheetName() + "]sheet页找到有效组序");
            } else {
                logger.info("获取到" + count + "组数据，开始分析数据");
            }
            XSSFFormulaEvaluator evaluator;
            XSSFSheet changeSheet = workbook.getSheet(Constant.EXCEL.CONFIG_SHEET_NAME);
            XSSFCell changeCell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.CHANGE_CELL_ADDR);
            int originalSheetNum = workbook.getNumberOfSheets();

            for (int i = 1; i <= count; i++) {
                logger.info("============================================开始处理页码" + i);
                XSSFCell printCell = ExcelUtil.getCell(changeSheet, Constant.EXCEL.PRINT_CELL_ADDR);
                changeCell.setCellValue(i);
                evaluator = new XSSFFormulaEvaluator(workbook);
                evaluator.evaluateAll();
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
            XSSFSheet pic = workbook.getSheet("图片");
            XSSFDrawing drawing = pic.getDrawingPatriarch();
            List<XSSFShape> shapeList = drawing.getShapes();
            PackagePart packagePart = drawing.getPackagePart();
            List<POIXMLDocumentPart> relations = drawing.getRelations();
            for (XSSFShape shape : shapeList) {
                if (shape instanceof XSSFPicture) {
                    XSSFPicture picture = (XSSFPicture) shape;
                    XSSFClientAnchor clientAnchor = picture.getClientAnchor();
                    clientAnchor.getCol1();
                    clientAnchor.getCol2();
                    clientAnchor.getRow1();
                    clientAnchor.getRow2();
                }

                String shapeName = shape.getShapeName();
                XSSFAnchor anchor = shape.getAnchor();
                int dx1 = anchor.getDx1();
                int dx2 = anchor.getDx2();
                int dy1 = anchor.getDy1();
                int dy2 = anchor.getDy2();
                System.out.println("shapeName= " + shapeName + ",dx1 = " + dx1 + ", dx2 = " + dx2 + ", dy1 = " + dy1 + ", dy2 = " + dy2);
            }

            XSSFSheet gsheet = workbook.getSheet("管垫隐");
            XSSFDrawing gdrawing = gsheet.getDrawingPatriarch();
            List<XSSFShape> gshapeList = gdrawing.getShapes();
            for (XSSFShape shape : gshapeList) {
                if (shape instanceof XSSFPicture) {
                    XSSFPicture picture = (XSSFPicture) shape;
                    CTPicture ctPicture = picture.getCTPicture();
                    CTPictureNonVisual ctPictureNonVisual = ctPicture.getNvPicPr();
                    CTNonVisualPictureProperties ctNonVisualPictureProperties = ctPictureNonVisual.getCNvPicPr();
                    CTOfficeArtExtensionList ctOfficeArtExtensionList = ctNonVisualPictureProperties.getExtLst();
//                    List<CTOfficeArtExtension> ctofficeArtExtensionList = ctOfficeArtExtensionList.getExtList();
//                    for (CTOfficeArtExtension ctOfficeArtExtension : ctofficeArtExtensionList) {
//                        ctOfficeArtExtension.getUri();
//                    }
//                    CTShapeProperties ctShapeProperties = ctPicture.getSpPr();
                }
                shape.getAnchor();
            }
            XSSFName xssfName = workbook.getNames("道路垫层").get(0);
//            xssfName.setRefersToFormula("INDEX(图片!$B:$B,MATCH(IF(组1管垫隐!$X$5=1,图片!$A$4,IF(组1管垫隐!$X$6=1,图片!$A$3,图片!$A$2)),图片!$A:$A,0))");
            int te = 123;

            XSSFFormulaEvaluator evaluator;
            for (int i = 1; i < 3; i++) {
                evaluator = new XSSFFormulaEvaluator(workbook);
                evaluator.evaluateAll();
                XSSFSheet oldSheet = workbook.getSheet("管垫隐");
                XSSFCell cell = ExcelUtil.getCell(oldSheet, "X5");
                cell.setCellType(CellType.STRING);
                cell.setCellValue((i - 1) + "");
                String newSheetName = "组" + i + oldSheet.getSheetName();
                ExcelUtil.cloneSheet(oldSheet, newSheetName);
            }
//            workbook.removeSheetAt(1);


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
