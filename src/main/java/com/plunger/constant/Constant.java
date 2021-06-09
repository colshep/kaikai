package com.plunger.constant;

import com.plunger.service.DictService;
import com.plunger.service.impl.ExcelServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class Constant {

    private static final Logger logger = LoggerFactory.getLogger(Constant.class);

    private static DictService dictService;

    @Resource
    public void setDictService(DictService dictService) {
        Constant.dictService = dictService;
    }

    @PostConstruct
    public void init(){
        try {
            refresh();
        } catch (Exception e) {
            logger.error("加载配置文件失败，请联系管理员");
            e.printStackTrace();
        }
    }

    public static void refresh() throws Exception {
        FILEPATH.UPLOADPATH = dictService.findValueByTypeAndName("system", "uploadPath");
        FILEPATH.RESULTPATH = dictService.findValueByTypeAndName("system", "resultPath");
        EXCEL.BASIC.SHEETNAME = dictService.findValueByTypeAndName("excel-basic", "sheetName");
        EXCEL.BASIC.PRINTCELLADDR = dictService.findValueByTypeAndName("excel-basic", "printCellAddr");
        EXCEL.BASIC.YUANCELLADDR = dictService.findValueByTypeAndName("excel-basic", "yuanCellAddr");
        EXCEL.BASIC.FANGCELLADDR = dictService.findValueByTypeAndName("excel-basic", "fangCellAddr");
        EXCEL.BASIC.JINCELLADDR = dictService.findValueByTypeAndName("excel-basic", "jinCellAddr");
        EXCEL.DATA.SHEETNAME = dictService.findValueByTypeAndName("excel-data", "sheetName");
        EXCEL.YUAN.SHEETNAMES = dictService.findValueByTypeAndName("excel-yuan", "sheetNames");
        EXCEL.FANG.SHEETNAMES = dictService.findValueByTypeAndName("excel-fang", "sheetNames");
        EXCEL.JIN.SHEETNAMES = dictService.findValueByTypeAndName("excel-jin", "sheetNames");
    }


    /**
     * 数据源
     */
    public static final class DATASOURCE {
        public static final String DEFAULT = "plunger";
        public static final String PLUNGER = "plunger";
        public static final String KAIKAI = "kaikai";
    }

    public static final class DICT {
        public static final class TYPE {
            public static final String STATE = "state";
            public static final String ROLE = "role";
            public static final String DEPT = "dept";
        }
    }

    public static final class EXCEL {
        public static final class BASIC {
            public static String SHEETNAME;
            public static String PRINTCELLADDR;
            public static String YUANCELLADDR;
            public static String FANGCELLADDR;
            public static String JINCELLADDR;
        }

        public static final class DATA {
            public static String SHEETNAME;
        }

        public static final class YUAN {
            public static String SHEETNAMES;
        }

        public static final class FANG {
            public static String SHEETNAMES;
        }

        public static final class JIN {
            public static String SHEETNAMES;
        }
    }

    public static final class FILEPATH {
        public static String UPLOADPATH;
        public static String RESULTPATH;
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
