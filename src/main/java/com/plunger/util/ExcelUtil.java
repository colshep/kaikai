package com.plunger.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.math.BigDecimal;

public class ExcelUtil {

    public static String getCellValue(XSSFCell cell) {
        //判断是否为null或空串
        if (cell == null || cell.toString().trim().equals("")) {
            return "";
        }
        String cellValue = "";

        CellType cellType = cell.getCellType();
        if (cellType == CellType.FORMULA) { //表达式类型
            XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(cell.getSheet().getWorkbook());
            cellType = evaluator.evaluate(cell).getCellType();
        }

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
