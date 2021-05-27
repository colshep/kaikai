package com.plunger.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JSONUtil {

    private static final Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    /**
     * 根据class去除不存在的属性，并将大小写和下划线调整为与class.field一致
     *
     * @param jsonObject
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> JSONObject formatProperties(JSONObject jsonObject, Class<T> clazz) {
        JSONObject copy = JSONObject.fromObject(jsonObject.toString());
        Field[] fields = clazz.getDeclaredFields();
        copy.keySet().forEach((key) -> {
            boolean isValid = false;
            for (Field field : fields) {
                String formatedFieldName = field.getName().toLowerCase().replace("_","");
                String formatedKey = key.toString().toLowerCase().replace("_","");
                if (jsonObject.get(key) != null && formatedFieldName.equals(formatedKey)) {
                    if (!field.getName().equals(key.toString())) {
                        // 调整大小写与bean保持一致
                        jsonObject.put(field.getName(), jsonObject.get(key));
                        jsonObject.remove(key);
                    }
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                jsonObject.remove(key);
            }
        });
        return jsonObject;
    }

    public static <T> JSONArray formatProperties(JSONArray jsonArray, Class<T> clazz) {
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonArray.set(i, formatProperties(jsonArray.getJSONObject(i), clazz));
        }
        return jsonArray;
    }

    public static <T> T toBeanFormatProperties(JSONObject jsonObject, Class<T> clazz) {
        return (T) JSONObject.toBean(formatProperties(jsonObject, clazz), clazz);
    }

    public static <T> List<T> toBeanListFormatProperties(JSONArray jsonArray, Class<T> clazz) {
        List<T> returnList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            returnList.add(toBeanFormatProperties(obj, clazz));
        }
        return returnList;
    }
}
