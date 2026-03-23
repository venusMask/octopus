package org.venus.octopus.common.utils;

/**
 * String utility class.
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Checks if the string is null or empty.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Checks if the string is null, empty, or contains only whitespace.
     */
    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    /**
     * Checks if the string is not empty (neither null nor empty).
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Checks if the string is not blank.
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Truncates the string to the specified length, appending an ellipsis if it
     * exceeds that length.
     *
     * @param str
     *            The original string
     * @param maxLen
     *            The maximum length
     * @return The truncated string
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
     * Capitalizes the first letter of the string.
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
