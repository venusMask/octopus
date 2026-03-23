package org.venus.octopus.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Collection utility class.
 */
public final class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * Checks if the collection is null or empty.
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Checks if the collection is not empty.
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Gets the first element in the collection, or null if it does not exist.
     */
    public static <T> T firstOrNull(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * Gets the last element in the collection, or null if it does not exist.
     */
    public static <T> T lastOrNull(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    /**
     * Safely gets the first element in the collection.
     */
    public static <T> Optional<T> first(List<T> list) {
        if (isEmpty(list)) {
            return Optional.empty();
        }
        return Optional.of(list.get(0));
    }
}
