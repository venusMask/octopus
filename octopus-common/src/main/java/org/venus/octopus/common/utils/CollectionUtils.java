package org.venus.octopus.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 集合工具类
 */
public final class CollectionUtils {

    private CollectionUtils() {}

    /**
     * 判断集合是否为 null 或空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否非空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 获取集合中的第一个元素，不存在则返回 null
     */
    public static <T> T firstOrNull(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 获取集合中的最后一个元素，不存在则返回 null
     */
    public static <T> T lastOrNull(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    /**
     * 安全地获取集合中的第一个元素
     */
    public static <T> Optional<T> first(List<T> list) {
        if (isEmpty(list)) {
            return Optional.empty();
        }
        return Optional.of(list.get(0));
    }
}
