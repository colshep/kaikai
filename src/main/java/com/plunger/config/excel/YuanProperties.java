package com.plunger.config.excel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "excel.yuan")
public class YuanProperties {

    private String sheetNames;

    public String getSheetNames() {
        return sheetNames;
    }

    public void setSheetNames(String sheetNames) {
        this.sheetNames = sheetNames;
    }
}
