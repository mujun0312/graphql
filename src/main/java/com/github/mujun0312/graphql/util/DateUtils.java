package com.github.mujun0312.graphql.util;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiang.zhang
 * @date 2023/6/9 5:44 下午
 */
public class DateUtils {

    private static final Object LOCK_OBJ = new Object();

    private static final Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<>();

    private DateUtils() {

    }

    public static SimpleDateFormat getSdf(String pattern) {
        ThreadLocal<SimpleDateFormat> threadLocal = sdfMap.get(pattern);
        if (threadLocal == null) {
            synchronized (LOCK_OBJ) {
                threadLocal = sdfMap.get(pattern);
                if (threadLocal == null) {
                    threadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat(pattern));
                    sdfMap.put(pattern, threadLocal);
                }
            }
        }
        return threadLocal.get();
    }
}
