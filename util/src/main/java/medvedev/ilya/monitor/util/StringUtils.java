package medvedev.ilya.monitor.util;

public class StringUtils {
    public static char lastChar(final String string) {
        final int stringLength = string.length();
        final int stringLastCharIndex = stringLength - 1;

        return string.charAt(stringLastCharIndex);
    }
}
