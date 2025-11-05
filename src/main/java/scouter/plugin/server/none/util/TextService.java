package scouter.plugin.server.none.util;

import scouter.lang.TextTypes;
import scouter.lang.pack.TextPack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Text dictionary service for converting hash values to actual text strings
 * Similar to scouter.client.model.TextProxy but for server-side plugins
 */
public class TextService {

    // Text type별로 hash -> text 매핑을 저장
    private static final Map<String, Map<Integer, String>> textCache = new ConcurrentHashMap<String, Map<Integer, String>>();

    static {
        // 모든 텍스트 타입에 대해 캐시 초기화
        textCache.put(TextTypes.SERVICE, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.SQL, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.METHOD, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.ERROR, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.APICALL, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.OBJECT, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.REFERER, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.USER_AGENT, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.GROUP, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.CITY, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.SQL_TABLES, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.MARIA, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.LOGIN, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.DESC, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.WEB, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.HASH_MSG, new ConcurrentHashMap<Integer, String>());
        textCache.put(TextTypes.STACK_ELEMENT, new ConcurrentHashMap<Integer, String>());
    }

    /**
     * TextPack을 받아서 캐시에 저장
     */
    public static void put(TextPack pack) {
        if (pack == null || pack.xtype == null) {
            return;
        }

        Map<Integer, String> cache = textCache.get(pack.xtype);
        if (cache != null) {
            cache.put(pack.hash, pack.text);
        }
    }

    /**
     * Hash 값을 실제 텍스트로 변환
     * @param textType 텍스트 타입 (TextTypes 상수 사용)
     * @param hash hash 값
     * @return 실제 텍스트 (없으면 hash 값을 문자열로 반환)
     */
    public static String getText(String textType, int hash) {
        Map<Integer, String> cache = textCache.get(textType);
        if (cache != null) {
            String text = cache.get(hash);
            if (text != null) {
                return text;
            }
        }
        // 캐시에 없으면 hash 값을 문자열로 반환
        return String.valueOf(hash);
    }

    /**
     * Service 이름 가져오기
     */
    public static String getServiceName(int hash) {
        return getText(TextTypes.SERVICE, hash);
    }

    /**
     * SQL 문 가져오기
     */
    public static String getSql(int hash) {
        return getText(TextTypes.SQL, hash);
    }

    /**
     * Method 이름 가져오기
     */
    public static String getMethodName(int hash) {
        return getText(TextTypes.METHOD, hash);
    }

    /**
     * Error 메시지 가져오기
     */
    public static String getErrorMessage(int hash) {
        return getText(TextTypes.ERROR, hash);
    }

    /**
     * API Call 이름 가져오기
     */
    public static String getApiCallName(int hash) {
        return getText(TextTypes.APICALL, hash);
    }

    /**
     * Object 이름 가져오기
     */
    public static String getObjectName(int hash) {
        return getText(TextTypes.OBJECT, hash);
    }

    /**
     * Hash Message 가져오기 (thread name 등)
     */
    public static String getHashMessage(int hash) {
        return getText(TextTypes.HASH_MSG, hash);
    }

    /**
     * 캐시 통계 정보
     */
    public static String getCacheStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("[TextService Cache Stats]\n");
        for (Map.Entry<String, Map<Integer, String>> entry : textCache.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue().size()).append(" entries\n");
        }
        return sb.toString();
    }

    /**
     * 특정 타입의 캐시 크기 확인
     */
    public static int getCacheSize(String textType) {
        Map<Integer, String> cache = textCache.get(textType);
        return cache != null ? cache.size() : 0;
    }

    /**
     * 캐시 초기화
     */
    public static void clearCache() {
        for (Map<Integer, String> cache : textCache.values()) {
            cache.clear();
        }
    }

    /**
     * 특정 타입의 캐시만 초기화
     */
    public static void clearCache(String textType) {
        Map<Integer, String> cache = textCache.get(textType);
        if (cache != null) {
            cache.clear();
        }
    }
}
