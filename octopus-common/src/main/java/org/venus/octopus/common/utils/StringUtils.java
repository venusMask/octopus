package org.venus.octopus.common.utils;

/**
 * 字符串工具类
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * 判断字符串是否为 null 或空字符串
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串是否为 null、空字符串或仅含空白字符
     */
    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    /**
     * 判断字符串是否非空（非 null 且非空字符串）
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否非空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 截断字符串到指定长度，超出部分追加省略号
     *
     * @param str
     *            原始字符串
     * @param maxLen
     *            最大长度
     * @return 截断后的字符串
     */
    public static String truncate(String str, int maxLen) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLen) {
            return str;
        }
        return str.substring(0, maxLen) + "...";
    }

    /**
     * 将字符串首字母大写
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
