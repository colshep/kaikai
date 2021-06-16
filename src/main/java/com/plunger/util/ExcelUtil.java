package com.plunger.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    public static void cloneSheet(XSSFSheet source, String newSheetName) throws Exception {
        XSSFWorkbook workbook = source.getWorkbook();
        XSSFSheet sheet = workbook.cloneSheet(workbook.getSheetIndex(source));

        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                XSSFCell cell = row.getCell(j);
                // 转义公式为纯文本
                if (cell != null && CellType.FORMULA == cell.getCellType()) {
                    XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(cell.getSheet().getWorkbook());
                    CellValue cellValue = evaluator.evaluate(cell);
                    CellType cellType = cellValue.getCellType();
                    cell.setCellType(cellType);

                    if (cellType == CellType.STRING) {
                        cell.setCellValue(cellValue.getStringValue());
                    } else if (cellType == CellType.BOOLEAN) {
                        cell.setCellValue(cellValue.getBooleanValue());
                    } else if (cellType == CellType.NUMERIC) {
                        cell.setCellValue(cellValue.getNumberValue());
                    } else if (cellType == CellType.ERROR) {
                        cell.setCellValue("ERROR" + cellValue.getErrorValue());
                        String msg = "解析公式失败,sheet=" + newSheetName + ",row=" + (i + 1) + ",col=" + (j + 1);
                        logger.error(msg);
//                        throw new Exception(msg);
                    }
                }
            }
        }

        // 复制打印区域
//        String[] printAreaArr = workbook.getPrintArea(workbook.getSheetIndex(source)).split("!");
//        workbook.setPrintArea(workbook.getSheetIndex(sheet), printAreaArr[1]);
//
//        // 复制其他打印设置
//        clonePrintSetup(source, sheet);
//        if (sheet instanceof XSSFSheet) {
//            XSSFSheet xssfSheet = (XSSFSheet) sheet;
//            //After cloning the cloned sheet has relation to the same
//            //"/xl/printerSettings/printerSettings[N].bin" package part as the source sheet had.
//            //This is wrong. So we need to repair.
//            ExcelUtil.repairCloningPrinterSettings(xssfSheet);
//        }

        // 继承缩放
//        sheet.getPrintSetup().setScale(source.getPrintSetup().getScale());

        workbook.setSheetName(workbook.getSheetIndex(sheet), newSheetName);
        sheet.setActiveCell(new CellAddress(0, 0));
    }

    public static void copySheets(XSSFSheet newSheet, XSSFSheet sheet, boolean copyStyle) throws Exception {
        int maxColumnNum = 0;
        List<CellRangeAddress> mergedRegions = new ArrayList<>();
        Map<Integer, CellStyle> styleMap = (copyStyle) ? new HashMap<>() : null;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            XSSFRow srcRow = sheet.getRow(i);
            XSSFRow destRow = newSheet.createRow(i);
            if (srcRow != null) {
                copyRow(sheet, newSheet, srcRow, destRow, styleMap, mergedRegions);
                if (srcRow.getLastCellNum() > maxColumnNum) {
                    maxColumnNum = srcRow.getLastCellNum();
                }
            }
        }
        for (int i = 0; i <= maxColumnNum; i++) {    //设置列宽
            newSheet.setColumnWidth(i, sheet.getColumnWidth(i));
        }
        newSheet.setTabColor(sheet.getTabColor());

        int oldSheetIndex = sheet.getWorkbook().getSheetIndex(sheet.getSheetName());
        int newSheetIndex = newSheet.getWorkbook().getSheetIndex(newSheet.getSheetName());

        String[] printAreaArr = sheet.getWorkbook().getPrintArea(oldSheetIndex).split("!");
        newSheet.getWorkbook().setPrintArea(newSheetIndex, printAreaArr[1]);

        XSSFPrintSetup oldPrintSetup = sheet.getPrintSetup();
        XSSFPrintSetup newPrintSetup = sheet.getPrintSetup();

        clonePrintSetup(sheet, newSheet);
        if (newSheet instanceof XSSFSheet) {
            XSSFSheet xssfSheet = (XSSFSheet) newSheet;
            //After cloning the cloned sheet has relation to the same
            //"/xl/printerSettings/printerSettings[N].bin" package part as the source sheet had.
            //This is wrong. So we need to repair.
            ExcelUtil.repairCloningPrinterSettings(xssfSheet);
        }

    }

    public static void copyRow(XSSFSheet srcSheet, XSSFSheet destSheet, XSSFRow srcRow, XSSFRow destRow,
                               Map<Integer, CellStyle> styleMap, List<CellRangeAddress> mergedRegions) {
        destRow.setHeight(srcRow.getHeight());
        int deltaRows = destRow.getRowNum() - srcRow.getRowNum(); //如果copy到另一个sheet的起始行数不同
        for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
            XSSFCell oldCell = srcRow.getCell(j); // old cell
            XSSFCell newCell = destRow.getCell(j); // new cell
            if (oldCell != null) {
                if (newCell == null) {
                    newCell = destRow.createCell(j);
                }
                copyCell(oldCell, newCell, styleMap);
                CellRangeAddress mergedRegion = getMergedRegion(srcSheet, srcRow.getRowNum(), (short) oldCell.getColumnIndex());
                if (mergedRegion != null) {
                    CellRangeAddress newMergedRegion = new CellRangeAddress(
                            mergedRegion.getFirstRow() + deltaRows,
                            mergedRegion.getLastRow() + deltaRows,
                            mergedRegion.getFirstColumn(),
                            mergedRegion.getLastColumn());
                    if (isNewMergedRegion(newMergedRegion, mergedRegions)) {
                        mergedRegions.add(newMergedRegion);
                        destSheet.addMergedRegion(newMergedRegion);
                    }
                }
            }
        }
    }

    /**
     * 把原来的Sheet中cell（列）的样式和数据类型复制到新的sheet的cell（列）中
     *
     * @param oldCell
     * @param newCell
     * @param styleMap
     */
    public static void copyCell(XSSFCell oldCell, XSSFCell newCell, Map<Integer, CellStyle> styleMap) {
        if (styleMap != null) {
            if (oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook()) {
                newCell.setCellStyle(oldCell.getCellStyle());
            } else {
                int stHashCode = oldCell.getCellStyle().hashCode();
                CellStyle newCellStyle = styleMap.get(stHashCode);
                if (newCellStyle == null) {
                    newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();
                    newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
                    styleMap.put(stHashCode, newCellStyle);
                }
                newCell.setCellStyle(newCellStyle);
            }
        }
        newCell.setCellValue(getCellValue(oldCell));
    }

    // 获取merge对象
    public static CellRangeAddress getMergedRegion(XSSFSheet sheet, int rowNum, short cellNum) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress merged = sheet.getMergedRegion(i);
            if (merged.isInRange(rowNum, cellNum)) {
                return merged;
            }
        }
        return null;
    }

    private static boolean isNewMergedRegion(CellRangeAddress newMergedRegion, List<CellRangeAddress> mergedRegions) {
        for (int i = 0; i < mergedRegions.size(); i++) {
            CellRangeAddress oldMergedRegion = mergedRegions.get(i);
            if (oldMergedRegion.getFirstRow() == newMergedRegion.getFirstRow() && oldMergedRegion.getFirstColumn() == newMergedRegion.getFirstColumn()) {
                return false;
            }
        }
        return true;
    }


    public static String getCellValue(XSSFSheet sheet, String R1C1Addr) {
        return getCellValue(getCell(sheet, R1C1Addr));
    }

    public static String getCellValue(XSSFCell cell) {
        //判断是否为null或空串
        if (cell == null || cell.toString().trim().equals("")) {
            return "";
        }
        String cellValue = "";
        CellType cellType = cell.getCellType();

        switch (cellType) {
            case STRING: //字符串类型
                cellValue = cell.getStringCellValue().trim();
                cellValue = StringUtils.isEmpty(cellValue) ? "" : cellValue;
                break;
            case BOOLEAN:  //布尔类型
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case NUMERIC: //数值类型
                //stripTrailingZeros()：去除末尾多余的0，toPlainString()：输出时不用科学计数法
                cellValue = new BigDecimal(String.valueOf(cell.getNumericCellValue())).stripTrailingZeros().toPlainString();
                break;
            case FORMULA: //数值类型
                XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(cell.getSheet().getWorkbook());
                cellValue = getCellValue(evaluator.evaluate(cell));
                break;
            default: //其它类型，取空串吧
                cellValue = "";
                break;
        }
        return cellValue;
    }

    public static String getCellValue(CellValue cellValueClazz) {
        String cellValue = "";
        CellType cellType = cellValueClazz.getCellType();
        switch (cellType) {
            case STRING: //字符串类型
                cellValue = cellValueClazz.getStringValue();
                cellValue = StringUtils.isEmpty(cellValue) ? "" : cellValue;
                break;
            case BOOLEAN:  //布尔类型
                cellValue = String.valueOf(cellValueClazz.getBooleanValue());
                break;
            case NUMERIC: //数值类型
                //stripTrailingZeros()：去除末尾多余的0，toPlainString()：输出时不用科学计数法
                cellValue = new BigDecimal(String.valueOf(cellValueClazz.getNumberValue())).stripTrailingZeros().toPlainString();
                break;
            default: //其它类型，取空串吧
                cellValue = "";
                break;
        }
        return cellValue;
    }


    public static XSSFCell getCell(XSSFSheet sheet, String R1C1Addr) {
        int[] addrNumArr = resloveR1C1Addr(R1C1Addr);
        XSSFRow row = sheet.getRow(addrNumArr[0] - 1);
        XSSFCell cell = row.getCell(addrNumArr[1] - 1);
        return cell;
    }

    public static int[] resloveR1C1Addr(String R1C1Addr) {
        char[] addrArr = R1C1Addr.toCharArray();
        String colStr = "";
        String rowStr = "";
        int splitIndex = 0;
        for (int i = 0; i < addrArr.length; i++) {
            char c = addrArr[i];
            int asc = Integer.valueOf(c);
            if (48 <= asc && asc <= 57) {
                splitIndex = i;
                break;
            }
        }
        colStr = R1C1Addr.substring(0, splitIndex);
        rowStr = R1C1Addr.substring(splitIndex, R1C1Addr.length());
        int[] addrNumArr = new int[2];
        addrNumArr[0] = Integer.valueOf(rowStr);
        addrNumArr[1] = R1C1ToNum(colStr);
        return addrNumArr;
    }

    public static int R1C1ToNum(String r1c1) {
        char[] addrArr = r1c1.toCharArray();
        int res = 0;
        for (int i = 0; i < addrArr.length; i++) {
            char c = addrArr[i];
            int a = (Integer.valueOf(c)) - 64;
            res += Math.pow(26, addrArr.length - i - 1) * a;
        }
        return res;
    }

    //method to transfer InputStream to OutputStream
    //to work using Java 8 since InputStream.transferTo needs at least Java 9
    static void transferInputStreamToOutputStream(InputStream in, OutputStream out) throws Exception {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }

    //method for binary cloning a PackagePart
    //works only for part names which ends with Idx followed by dot followed by extension
    //throws Exception when not successful
    static PackagePart clonePackagePart(PackagePart sourcePart, String contentType) throws Exception {
        OPCPackage oPCPackage = sourcePart.getPackage();
        String sourcePartName = sourcePart.getPartName().getName();
        String destinationPartName = sourcePartName;
        String[] sourcePartNameSplitExtension = sourcePartName.split("\\.");
        if (sourcePartNameSplitExtension.length == 2) {
            sourcePartName = sourcePartNameSplitExtension[0];
            String sourcePartNameExtension = sourcePartNameSplitExtension[1];
            int i = sourcePartName.length();
            while (i > 0 && Character.isDigit(sourcePartName.charAt(i - 1))) {
                i--;
            }
            int idx = Integer.valueOf(sourcePartName.substring(i));
            idx++;
            destinationPartName = sourcePartName.substring(0, i) + idx + "." + sourcePartNameExtension;
        }
        PackagePartName partName = PackagingURIHelper.createPartName(destinationPartName);
        PackagePart destinationPart = oPCPackage.createPart(partName, contentType);
        InputStream in = sourcePart.getInputStream();
        OutputStream out = destinationPart.getOutputStream();
        //in.transferTo(out); // at least Java 9 needed
        transferInputStreamToOutputStream(in, out);
        out.close();
        return destinationPart;
    }


    //method for repairing the relation from sheet to "/xl/printerSettings/printerSettings[N].bin" package part
    //clones "/xl/printerSettings/printerSettings[N].bin" package part
    //repairs the wrong cloned relation to the old "/xl/printerSettings/printerSettings[N].bin" package part
    //works using apache poi 4.1.2
    //must be changed when Workbook.cloneSheet changes in later versions
    public static void repairCloningPrinterSettings(XSSFSheet sheet) throws Exception {
        for (POIXMLDocumentPart.RelationPart relationPart : sheet.getRelationParts()) {
            String contentType = relationPart.getDocumentPart().getPackagePart().getContentType();
            if ("application/vnd.openxmlformats-officedocument.spreadsheetml.printerSettings".equals(contentType)) {
                System.out.println(relationPart.getRelationship());
                //clone the "/xl/printerSettings/printerSettings[N].bin" package part
                PackagePart sourcePart = relationPart.getDocumentPart().getPackagePart();
                PackagePart destinationPart = clonePackagePart(sourcePart, contentType);
                //remove the wrong cloned relation to the old "/xl/printerSettings/printerSettings[N].bin" package part
                relationPart.getRelationship().getSource().removeRelationship(relationPart.getRelationship().getId());
                //add the relation to the new "/xl/printerSettings/printerSettings[N].bin" package part
                PackageRelationship relationship = sheet.getPackagePart().addRelationship(
                        destinationPart.getPartName(),
                        TargetMode.INTERNAL,
                        XSSFRelation.PRINTER_SETTINGS.getRelation());
                //set Id of relation to the new "/xl/printerSettings/printerSettings[N].bin" package part
                //in sheet's page setup
                if (sheet.getCTWorksheet().getPageSetup() == null) sheet.getCTWorksheet().addNewPageSetup();
                sheet.getCTWorksheet().getPageSetup().setId(relationship.getId());
            }
        }
    }

    //method for cloning the PrintSetup from Sheet source to Sheet clone
    //uses java.beans.* and java.lang.reflect.Method to get all values using getters from sourcePrintSetup
    //and set those values to clonePrintSetup using the appropriate setters
    //works only for getters without parameters and setters having exact one parameter
    //throws Exception when not successful
    public static void clonePrintSetup(Sheet source, Sheet clone) throws Exception {
        PrintSetup sourcePrintSetup = source.getPrintSetup();
        PrintSetup clonePrintSetup = clone.getPrintSetup();
        for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(PrintSetup.class).getPropertyDescriptors()) {
            Method getMethod = propertyDescriptor.getReadMethod();
            Object value = null;
            if (getMethod != null && getMethod.getParameterTypes().length == 0) {
                value = getMethod.invoke(sourcePrintSetup);
                Method setMethod = propertyDescriptor.getWriteMethod();
                if (setMethod != null && setMethod.getParameterTypes().length == 1) {
                    setMethod.invoke(clonePrintSetup, value);
//                    System.out.println(setMethod + ": " + value);
                }
            }
        }
//        System.out.println("clonePrintSetup finish");
    }

    public static void main(String[] args) {
        // A=26^0*1=1
        System.out.println("A=" + R1C1ToNum("A"));
        // Z=26^0*26=26
        System.out.println("Z=" + R1C1ToNum("Z"));
        // AA=26^1*1+1=27
        System.out.println("AA=" + R1C1ToNum("AA"));
        // AZ=26^1*1+26=52
        System.out.println("AZ=" + R1C1ToNum("AZ"));
        // ZA=26^1*26+1=677
        System.out.println("ZA=" + R1C1ToNum("ZA"));
        // AAA=26^2*1+26^1*1+26^0*1=703
        System.out.println("AAA=" + R1C1ToNum("AAA"));

        System.out.println("resloveR1C1Addr(A1): row=" + resloveR1C1Addr("A1")[0] + ",col=" + resloveR1C1Addr("A1")[1]);
        System.out.println("resloveR1C1Addr(Z22): row=" + resloveR1C1Addr("Z22")[0] + ",col=" + resloveR1C1Addr("Z22")[1]);
    }
}
