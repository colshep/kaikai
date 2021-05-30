package com.plunger.config.excel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@ConfigurationProperties(prefix = "excel")
public class ExcelProperties {

    @Resource
    private BasicProperties basicProperties;
    @Resource
    private DataProperties dataProperties;
    @Resource
    private YuanProperties yuanProperties;
    @Resource
    private FangProperties fangProperties;
    @Resource
    private JinProperties jinProperties;

    public BasicProperties getBasicProperties() {
        return basicProperties;
    }

    public void setBasicProperties(BasicProperties basicProperties) {
        this.basicProperties = basicProperties;
    }

    public DataProperties getDataProperties() {
        return dataProperties;
    }

    public void setDataProperties(DataProperties dataProperties) {
        this.dataProperties = dataProperties;
    }

    public YuanProperties getYuanProperties() {
        return yuanProperties;
    }

    public void setYuanProperties(YuanProperties yuanProperties) {
        this.yuanProperties = yuanProperties;
    }

    public FangProperties getFangProperties() {
        return fangProperties;
    }

    public void setFangProperties(FangProperties fangProperties) {
        this.fangProperties = fangProperties;
    }

    public JinProperties getJinProperties() {
        return jinProperties;
    }

    public void setJinProperties(JinProperties jinProperties) {
        this.jinProperties = jinProperties;
    }
}
