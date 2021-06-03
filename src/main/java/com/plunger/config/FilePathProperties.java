package com.plunger.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file-path")
public class FilePathProperties {

    private String uploadPath;
    private String resultPath;

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getResultPath() {
        return resultPath;
    }

    public void setResultPath(String resultPath) {
        this.resultPath = resultPath;
    }

}
