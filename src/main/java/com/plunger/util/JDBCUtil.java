package com.plunger.util;

import com.plunger.constant.Constant;
import net.sf.json.JSONArray;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JDBCUtil implements ApplicationContextAware {

    /**
     * 单例模式
     */
    private static JDBCUtil instance = new JDBCUtil();

    private JDBCUtil() {
    }

    public static JDBCUtil getInstance() {
        return instance;
    }

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (JDBCUtil.applicationContext == null) {
            // 初始化 spring applicationContext
            JDBCUtil.applicationContext = applicationContext;
        }
    }

    private static JdbcTemplate plungerJdbcTemplate;

    /**
     * @param dateSourceName Constant.DATASOURCE.xxx
     * @return
     */
    public static JdbcTemplate getJdbcTemplate(String dateSourceName) {
        if (Constant.DATASOURCE.PLUNGER.equals(dateSourceName)) {
            if (instance.plungerJdbcTemplate == null) {
                instance.plungerJdbcTemplate = applicationContext.getBean("plungerJdbcTemplate", JdbcTemplate.class);
            }
            return instance.plungerJdbcTemplate;
        }
        return instance.plungerJdbcTemplate;
    }

    public static JdbcTemplate getJdbcTemplate() {
        return getJdbcTemplate(Constant.DATASOURCE.DEFAULT);
    }

    private static <T> String findDataSourceName(Class<T> clazz) {
        String className = clazz.getName();
        int index = className.indexOf(".bean.") + ".bean.".length();
        String result = className.substring(index);
        result = result.substring(0, result.indexOf("."));
        return result;
    }

    private static JSONArray queryForJSONArray(JdbcTemplate jdbcTemplate, String sql, Object... args) {
        return JSONArray.fromObject(jdbcTemplate.queryForList(sql.toLowerCase(), args));
    }

    public static JSONArray queryForJSONArray(String sql, Object... args) {
        return queryForJSONArray(getJdbcTemplate(), sql, args);
    }

    public static <T> JSONArray queryForJSONArray(Class<T> clazz, String sql, Object... args) {
        return queryForJSONArray(getJdbcTemplate(findDataSourceName(clazz)), sql, args);
    }

    public static <T> List<T> queryForList(Class<T> clazz, String sql, Object... args) {
        JSONArray array = queryForJSONArray(clazz, sql, args);
        return JSONUtil.toBeanListFormatProperties(array, clazz);
    }

    public static int update(String sql, Object... args) {
        return getJdbcTemplate().update(sql, args);
    }

    public static int getCount(String sql, Object... args) {
        JSONArray resArr = queryForJSONArray(sql, args);
        if (resArr != null && !resArr.isEmpty()) {
            return resArr.optJSONObject(0).optInt("count");
        }
        return 0;
    }
}
