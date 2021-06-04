package com.plunger.util;

import com.plunger.constant.Constant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashMap;

public class DictUtil {

    private static final HashMap<String, String> stateMap = new HashMap<String, String>() {
        {
            put("A0", "草稿");
        }
    };

    private static final HashMap<String, String> roleMap = new HashMap<String, String>() {
        {
            put("0", "系统");
        }
    };

    private static final HashMap<String, String> deptMap = new HashMap<String, String>();

    static {

    }

    private static HashMap<String, String> getMap(String type) {
        if (Constant.DICT.TYPE.STATE.equals(type)) {
            return stateMap;
        } else if (Constant.DICT.TYPE.ROLE.equals(type)) {
            return roleMap;
        } else if (Constant.DICT.TYPE.DEPT.equals(type)) {
            return deptMap;
        }
        return null;
    }

    public static String getName(String type, String key) {
        if (getMap(type) != null) {
            return getMap(type).get(key);
        }
        return "";
    }

    public static JSONArray getMapForPage(String type) {
        if (getMap(type) != null) {
            JSONArray returnArr = new JSONArray();
            getMap(type).forEach((key, name) -> {
                JSONObject item = new JSONObject();
                item.put("key", key);
                item.put("name", name);
                returnArr.add(item);
            });
            return returnArr;
        }
        return new JSONArray();
    }

    public static String getStateName(String key) {
        return getName(Constant.DICT.TYPE.STATE, key);
    }

    public static String getRoleName(String key) {
        return getName(Constant.DICT.TYPE.ROLE, key);
    }

    public static String getDeptName(String unid) {
        return getName(Constant.DICT.TYPE.DEPT, unid);
    }

}
