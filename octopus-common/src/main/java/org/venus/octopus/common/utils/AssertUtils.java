package org.venus.octopus.common.utils;

import java.util.Collection;
import java.util.Map;
import org.venus.octopus.common.exception.OctopusException;

/**
 * Parameter assertion utility class.
 * <p>
 * Throws {@link IllegalArgumentException} or {@link OctopusException} when an
 * assertion fails.
 * </p>
 */
public final class AssertUtils {

    private AssertUtils() {
    }

    /**
     * Asserts that the object is not null.
     *
     * @param obj
     *            The object to check
     * @param message
     *            The error message
     * @throws IllegalArgumentException
     *             If the object is null
     */
    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Asserts that the string is not empty.
     *
     * @param str
     *            The string to check
     * @param message
     *            The error message
     * @throws IllegalArgumentException
     *             If the string is null or empty
     */
    public static void notEmpty(String str, String message) {
        if (StringUtils.isEmpty(str)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Asserts that the collection is not empty.
     *
     * @param collection
     *            The collection to check
     * @param message
     *            The error message
     * @throws IllegalArgumentException
     *             If the collection is null or empty
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Asserts that the Map is not empty.
     *
     * @param map
     *            The Map to check
     * @param message
     *            The error message
     * @throws IllegalArgumentException
     *             If the Map is null or empty
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Asserts that the condition is true.
     *
     * @param condition
     *            The condition expression
     * @param message
     *            The error message
     * @throws IllegalArgumentException
     *             If the condition is false
     */
    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Asserts that the condition is false.
     *
     * @param condition
     *            The condition expression
     * @param message
     *            The error message
     * @throws IllegalArgumentException
     *             If the condition is true
     */
    public static void isFalse(boolean condition, String message) {
        if (condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
