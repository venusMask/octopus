package org.venus.octopus.common.utils;

import java.util.Collection;
import java.util.Map;
import org.venus.octopus.common.exception.OctopusException;

/**
 * 参数断言工具类
 * <p>断言失败时抛出 {@link IllegalArgumentException} 或 {@link OctopusException}。</p>
 */
public final class AssertUtils {

    private AssertUtils() {}

    /**
     * 断言对象不为 null
     *
     * @param obj     被检查的对象
     * @param message 错误信息
     * @throws IllegalArgumentException 如果对象为 null
     */
    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言字符串不为空
     *
     * @param str     被检查的字符串
     * @param message 错误信息
     * @throws IllegalArgumentException 如果字符串为 null 或空
     */
    public static void notEmpty(String str, String message) {
        if (StringUtils.isEmpty(str)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言集合不为空
     *
     * @param collection 被检查的集合
     * @param message    错误信息
     * @throws IllegalArgumentException 如果集合为 null 或空
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言 Map 不为空
     *
     * @param map     被检查的 Map
     * @param message 错误信息
     * @throws IllegalArgumentException 如果 Map 为 null 或空
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言条件为 true
     *
     * @param condition 条件表达式
     * @param message   错误信息
     * @throws IllegalArgumentException 如果条件为 false
     */
    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言条件为 false
     *
     * @param condition 条件表达式
     * @param message   错误信息
     * @throws IllegalArgumentException 如果条件为 true
     */
    public static void isFalse(boolean condition, String message) {
        if (condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
