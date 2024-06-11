package likeai.fun;

/**
 * @author likeai
 */
public class StringUtils {
    public static boolean hasText(String str) {
        return (str == null || str.isBlank());
    }

    public static void requireHasText(String str, String message) {
        if (hasText(str))
            throw new IllegalArgumentException(message);
    }

    public static void requireHasText(String str) {
        if (hasText(str))
            throw new IllegalArgumentException("Empty str, but it is required!");
    }
}
