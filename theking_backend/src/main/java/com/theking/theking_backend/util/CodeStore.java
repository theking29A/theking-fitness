package com.theking.theking_backend.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存验证码存储器（带过期时间）
 * key 格式: "类型:目标"  如 "register:admin" / "forgot:admin"
 */
public class CodeStore {

    private static final Map<String, CodeEntry> STORE = new ConcurrentHashMap<>();
    private static final long EXPIRE_MS = 5 * 60 * 1000; // 5分钟过期

    public static void put(String type, String target, String code) {
        STORE.put(type + ":" + target, new CodeEntry(code, System.currentTimeMillis()));
    }

    public static boolean verify(String type, String target, String code) {
        String key = type + ":" + target;
        CodeEntry entry = STORE.get(key);
        if (entry == null) return false;
        if (System.currentTimeMillis() - entry.timestamp > EXPIRE_MS) {
            STORE.remove(key);
            return false;
        }
        boolean ok = entry.code.equalsIgnoreCase(code);
        if (ok) STORE.remove(key); // 验证成功后删除
        return ok;
    }

    public static void remove(String type, String target) {
        STORE.remove(type + ":" + target);
    }

    private static class CodeEntry {
        String code;
        long timestamp;
        CodeEntry(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }
}
