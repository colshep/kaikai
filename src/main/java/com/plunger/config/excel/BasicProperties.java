package com.plunger.config.excel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "excel.basic")
public class BasicProperties {

    private String sheetName;
    private String printCellAddr;
    private String yuanCellAddr;
    private String fangCellAddr;
    private String jinCellAddr;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getPrintCellAddr() {
        return printCellAddr;
    }

    public void setPrintCellAddr(String printCellAddr) {
        this.printCellAddr = printCellAddr;
    }

    public String getYuanCellAddr() {
        return yuanCellAddr;
    }

    public void setYuanCellAddr(String yuanCellAddr) {
        this.yuanCellAddr = yuanCellAddr;
    }

    public String getFangCellAddr() {
        return fangCellAddr;
    }

    public void setFangCellAddr(String fangCellAddr) {
        this.fangCellAddr = fangCellAddr;
    }

    public String getJinCellAddr() {
        return jinCellAddr;
    }

    public void setJinCellAddr(String jinCellAddr) {
        this.jinCellAddr = jinCellAddr;
    }
}
