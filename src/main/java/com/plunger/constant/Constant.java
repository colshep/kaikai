package com.plunger.constant;

import com.plunger.config.FilePathProperties;
import com.plunger.config.excel.ExcelProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class Constant {

    private static ExcelProperties excelProperties;
    private static FilePathProperties filePathProperties;

    @Resource
    public void setExcelProperties(ExcelProperties excelProperties) {
        Constant.excelProperties = excelProperties;
    }
    @Resource
    public void setFilePathProperties(FilePathProperties filePathProperties) {
        Constant.filePathProperties = filePathProperties;
    }

    /**
     * 数据源
     */
    public static final class DATASOURCE {
        public static final String PLUNGER = "plunger";
    }

    public static final class DICT {
        public static final class TYPE {
            public static final String STATE = "state";
            public static final String ROLE = "role";
            public static final String DEPT = "dept";
            public static final String SOURCE = "source";
        }
    }

    public static final class EXCEL {
        public static final class BASIC {
            public static final String SHEETNAME = excelProperties.getBasicProperties().getSheetName();
            public static final String PRINTCELLADDR = excelProperties.getBasicProperties().getPrintCellAddr();
            public static final String YUANCELLADDR = excelProperties.getBasicProperties().getYuanCellAddr();
            public static final String FANGCELLADDR = excelProperties.getBasicProperties().getFangCellAddr();
            public static final String JINCELLADDR = excelProperties.getBasicProperties().getJinCellAddr();
        }

        public static final class DATA {
            public static final String SHEETNAME = excelProperties.getDataProperties().getSheetName();
        }

        public static final class YUAN {
            public static final String SHEETNAMES = excelProperties.getYuanProperties().getSheetNames();
        }

        public static final class FANG {
            public static final String SHEETNAMES = excelProperties.getFangProperties().getSheetNames();
        }

        public static final class JIN {
            public static final String SHEETNAMES = excelProperties.getJinProperties().getSheetNames();
        }
    }

    /**
     * session
     */
    public static final class SESSION {
        /**
         * 参数
         */
        public static final class ATTRIBUTE {
            public static final String USER = "user";

        }

        /**
         * 状态
         */
        public static final class STATE {

        }

    }

    /**
     * 性别,0-女,1-男
     */
    public static class SEX {
        public static final String EMALE = "0";
        public static final String MALE = "1";
    }

    /**
     * 用户类型
     */
    public static class USER {
        public static final class TYPE {

        }
    }

}
