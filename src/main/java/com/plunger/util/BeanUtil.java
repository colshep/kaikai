package com.plunger.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class BeanUtil {

    private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    public static <T> T copy(T origin, T target) {
        try {
            if (target == null) {
                target = (T) BeanUtils.cloneBean(origin);
            } else {
                BeanUtils.copyProperties(target, origin);
            }
        } catch (Exception e) {
            logger.error("BeanUtil copy error", e);
            return null;
        }
        return target;
    }

    /**
     * 将origin的属性合并入target，并返回target。
     * 当两个对象的属性都有值时，根据overwritten判断是否需要覆盖
     *
     * @param origin
     * @param target
     * @param overwritten
     * @param <T>
     * @return
     */
    public static <T> T merge(T origin, T target, boolean overwritten) {
        Class clazz = origin.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            try {
                if (field.get(target) != null && !StringUtils.isEmpty(field.get(target).toString()) && !overwritten) {
                    continue;
                }
                if (field.get(origin) != null) {
                    field.set(target, field.get(origin));
                }
            } catch (Exception e) {
                logger.error("BeanUtil merge error", e);
            }
        }
        return target;
    }
}
