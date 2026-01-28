package tech.minediamond.micanbt.SNBT;

final class Tokens {
    private Tokens() {
    }

    // Structural Delimiters
    static final char COMPOUND_BEGIN = '{';
    static final char COMPOUND_END = '}';
    static final char COMPOUND_KEY_VALUE_SEPARATOR = ':';
    static final char ARRAY_BEGIN = '[';
    static final char ARRAY_END = ']';
    static final char ARRAY_SIGNATURE_SEPARATOR = ';';
    static final char VALUE_SEPARATOR = ',';

    // Strings & Escaping
    static final char SINGLE_QUOTE = '\'';
    static final char DOUBLE_QUOTE = '"';
    static final char ESCAPE_MARKER = '\\';

    // Type suffix
    static final char TYPE_BYTE = 'b';
    static final char TYPE_SHORT = 's';
    static final char TYPE_INT = 'i';
    static final char TYPE_LONG = 'l';
    static final char TYPE_FLOAT = 'f';
    static final char TYPE_DOUBLE = 'd';

    // Type suffix (Uppercase)
    static final char TYPE_BYTE_UPPER = 'B';
    static final char TYPE_SHORT_UPPER = 'S';
    static final char TYPE_INT_UPPER = 'I';
    static final char TYPE_LONG_UPPER = 'L';
    static final char TYPE_FLOAT_UPPER = 'F';
    static final char TYPE_DOUBLE_UPPER = 'D';

    // Numeric Prefixes
    static final String BINARY_PREFIX = "0b";
    static final String BINARY_PREFIX_UPPER = "0B";
    static final String HEX_PREFIX = "0x";
    static final String HEX_PREFIX_UPPER = "0X";

    // Signedness
    static final char TYPE_SIGNED = 's';
    static final char TYPE_UNSIGNED = 'u';
    static final char TYPE_SIGNED_UPPER = 'S';
    static final char TYPE_UNSIGNED_UPPER = 'U';

    // Literals
    static final String LITERAL_TRUE = "true";
    static final String LITERAL_FALSE = "false";

    // Formatting Characters
    static final char SPACE = ' ';
    static final char NEWLINE = '\n';
    static final char CARRIAGE_RETURN = '\r';
    static final char TAB = '\t';
    static final char EOF = '\0'; // End of File

    // Internal Lookup Tables & Bitmasks
    private static final int FLAG_IS_DIGIT_EXT = 1;  // 0-9, -, .
    private static final int FLAG_MAY_NUMBER = 2;   // 0-9, -, ., e, E, +

    private static final boolean[] ALLOWED_CHARS = new boolean[128];
    private static final byte[] CHAR_FLAGS = new byte[128];
    private static final long FORMAT_CHARS_MASK = (1L << EOF) | (1L << TAB) | (1L << NEWLINE) | (1L << CARRIAGE_RETURN) | (1L << SPACE);

    static {
        for (char c = 'a'; c <= 'z'; c++) ALLOWED_CHARS[c] = true;
        for (char c = 'A'; c <= 'Z'; c++) ALLOWED_CHARS[c] = true;
        for (char c = '0'; c <= '9'; c++) ALLOWED_CHARS[c] = true;
        ALLOWED_CHARS['-'] = true;
        ALLOWED_CHARS['_'] = true;
        ALLOWED_CHARS['.'] = true;
        ALLOWED_CHARS['+'] = true;

        for (char c = '0'; c <= '9'; c++) {
            CHAR_FLAGS[c] |= FLAG_IS_DIGIT_EXT | FLAG_MAY_NUMBER;
        }

        CHAR_FLAGS['-'] |= FLAG_IS_DIGIT_EXT | FLAG_MAY_NUMBER;
        CHAR_FLAGS['.'] |= FLAG_IS_DIGIT_EXT | FLAG_MAY_NUMBER;
        CHAR_FLAGS['+'] |= FLAG_MAY_NUMBER;
        CHAR_FLAGS['e'] |= FLAG_MAY_NUMBER;
        CHAR_FLAGS['E'] |= FLAG_MAY_NUMBER;
    }

    /**
     * Determines if a character is valid for a string that doesn't require quotes.
     *
     * @param c The character to check.
     * @return True if the character is allowed without quotes.
     */
    static boolean isAllowedInUnquotedString(final char c) {
        return c < 128 && ALLOWED_CHARS[c];
    }

    /**
     * Checks if the given string needs to be wrapped in quotes for SNBT.
     *
     * @param s The string to evaluate.
     * @return True if the string is empty or contains characters that require quoting.
     */
    static boolean needQuotation(String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (!isAllowedInUnquotedString(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a character is a digit or a digit-related symbol <code>0-9 - .</code>.
     */
    static boolean isDigit(final char c) {
        return c < 128 && (CHAR_FLAGS[c] & FLAG_IS_DIGIT_EXT) != 0;
    }

    /**
     * Performs a fast check to see if a string potentially represents a number.
     * This is used to decide whether to attempt numeric parsing.
     *
     * @param s The string to check.
     * @return True if all characters are valid for a numeric literal.
     */
    static boolean mayNumber(String s) {
        if (s == null || s.isEmpty()) return false;

        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);

            if (c >= 128 || (CHAR_FLAGS[c] & FLAG_MAY_NUMBER) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a character is a formatting character
     * include <code>\0 \t \n \r space</code>
     */
    static boolean isFormatChar(final char c) {
        return c <= 32 && (FORMAT_CHARS_MASK & (1L << c)) != 0;
    }
}
