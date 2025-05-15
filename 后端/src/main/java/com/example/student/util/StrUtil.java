package com.example.student.util;

/**
 * 字符串工具类
 * 
 * @author example
 */
public class StrUtil {
    
    /**
     * 判断字符串是否为空或null
     *
     * @param str 待检查的字符串
     * @return 如果字符串为null或空字符串，返回true；否则返回false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
    
    /**
     * 判断字符串是否为非空且非null
     *
     * @param str 待检查的字符串
     * @return 如果字符串非null且非空字符串，返回true；否则返回false
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 判断字符串是否为空白字符串（null、空字符串或只包含空白字符）
     *
     * @param str 待检查的字符串
     * @return 如果字符串为null、空字符串或只包含空白字符，返回true；否则返回false
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 判断字符串是否为非空白字符串
     *
     * @param str 待检查的字符串
     * @return 如果字符串非null、非空字符串且不只包含空白字符，返回true；否则返回false
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /**
     * 获取字符串，如果为null则返回空字符串
     *
     * @param str 原始字符串
     * @return 非null的字符串
     */
    public static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }
    
    /**
     * 截取指定长度的字符串，超过长度的部分用省略号代替
     *
     * @param str 原始字符串
     * @param maxLength 最大长度
     * @return 截取后的字符串
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        
        if (str.length() <= maxLength) {
            return str;
        }
        
        return str.substring(0, maxLength) + "...";
    }
} 