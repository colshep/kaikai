package com.plunger.constant;

import com.plunger.service.DictService;
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
    public void init() {
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

        EXCEL.CHANGE_CELL_ADDR = dictService.findValueByTypeAndName("excel", "changeCellAddr");
        EXCEL.CONFIG_SHEET_NAME = dictService.findValueByTypeAndName("excel", "configSheetName");
        EXCEL.DATA_SHEET_NAME = dictService.findValueByTypeAndName("excel", "dataSheetName");
        EXCEL.PRINT_CELL_ADDR = dictService.findValueByTypeAndName("excel", "printCellAddr");
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
        public static String CHANGE_CELL_ADDR;
        public static String CONFIG_SHEET_NAME;
        public static String DATA_SHEET_NAME;
        public static String PRINT_CELL_ADDR;
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
