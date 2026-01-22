package tech.minediamond.lusternbt.SNBT;

import tech.minediamond.lusternbt.tag.builtin.*;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class SNBTReader {
    private final CharBuffer charBuffer;
    private final Tag tag;

    public SNBTReader(String SNBTText) {
        this.charBuffer = CharBuffer.wrap(SNBTText);
        try {
            tag = parseTag("");
        } catch (Exception e) {
            System.out.println("Error while parsing SNBTText: " + e);
            printlnFromStartToPosition("Error while parsing SNBTText: ");
            throw e;
        }
    }

    public SNBTReader(Path path) throws IOException {
        this.charBuffer = CharBuffer.wrap(Files.readString(path));
        tag = parseTag("");
    }

    public Tag getTag() {
        return tag;
    }

    static String fromStartToPosition(CharBuffer buf) {
        int pos = buf.position();          // 当前指针
        CharBuffer view = buf.duplicate(); // 共享内容，但 position/limit 独立
        view.position(0);
        view.limit(pos);
        return view.toString();            // toString() 输出 [position, limit)
    }

    void printlnFromStartToPosition(String msg) {
        System.out.println("custom message: " + msg + ", current processed: " + fromStartToPosition(charBuffer));
    }

    void printlnFromStartToPosition() {
        System.out.println("current processed: " + fromStartToPosition(charBuffer));
    }

    public void skipEmptyChar() {
        char c;
        while (charBuffer.hasRemaining()) {
            c = this.charBuffer.get();
            if (!Tokens.isFormatChar(c)) {
                charBuffer.position(this.charBuffer.position() - 1);
                return;
            }
        }
        throw new BufferUnderflowException();
    }

    private char peek() {
        char c = charBuffer.get();
        charBuffer.position(this.charBuffer.position() - 1);
        return c;
    }

    private char[] peek(int quantity) {
        charBuffer.mark();
        char[] cs = new char[quantity];
        int i = 0;
        char c;
        while (charBuffer.hasRemaining()) {
            c = this.charBuffer.get();
            cs[i++] = c;
            if (i == quantity) {
                charBuffer.reset();
                return cs;
            }
        }
        charBuffer.reset();
        throw new BufferUnderflowException();
    }

    private char consume() {
        return charBuffer.get();
    }

    private char[] consume(int quantity) {
        char[] cs = new char[quantity];
        int i = 0;
        char c;
        while (charBuffer.hasRemaining()) {
            c = this.charBuffer.get();
            cs[i++] = c;
            if (i == quantity) {
                return cs;
            }
        }
        throw new BufferUnderflowException();
    }

    private Tag parseTag(String name) {
        skipEmptyChar();
        char c = peek();
        return switch (c) {
            case Tokens.COMPOUND_BEGIN -> parseCompound(name);
            case Tokens.ARRAY_BEGIN -> parseArrayOrList(name);
            default -> parsePrimitive(name);
        };
    }

    private Tag parseArrayOrList(String name) {
        char[] first = peek(2);
        if (first[1] == Tokens.ARRAY_END) {
            consume(2); // `[]`
            return new ListTag(name);
        }
        char[] peek = peek(3);
        if (peek[2] == Tokens.ARRAY_SIGNATURE_SEPARATOR && "BIL".indexOf(peek[1]) != -1) {
            return parseTypedArray(peek[1], name);
        } else {
            return parseList(name);
        }
    }

    private Tag parseCompound(String name) {
        CompoundTag compoundTag = new CompoundTag(name);
        consume(); // `{`
        if (peek() == Tokens.COMPOUND_END) {
            consume(); // `}`
            return compoundTag;
        }

        while (peek() != Tokens.COMPOUND_END) {
            String subName = parseString();
            skipEmptyChar(); // empty char between `"` and `:`
            consume(); // `:`
            compoundTag.put(parseTag(subName));
            if (peek() == Tokens.VALUE_SEPARATOR) {
                consume(); // `,`
            }
            skipEmptyChar(); // skip empty char after `,` or something possible
        }

        consume(); // `}`
        return compoundTag;
    }

    private Tag parseTypedArray(char type, String name) {
        return switch (type) {
            case Tokens.TYPE_BYTE_UPPER -> parseByteArray(name);
            case Tokens.TYPE_INT_UPPER -> parseIntArray(name);
            case Tokens.TYPE_LONG_UPPER -> parseLongArray(name);
            default -> throw new IllegalStateException("Unexpected token: " + type);
        };
    }

    private ByteArrayTag parseByteArray(String name) {
        ByteArrayTag byteArrayTag = new ByteArrayTag(name);
        consume(3); // `[B;`
        skipEmptyChar(); // skip any possible empty char
        if (peek() == Tokens.ARRAY_END) {
            consume(); // `]`
            return byteArrayTag;
        }
        ArrayList<Byte> bytes = new ArrayList<>();
        while (peek() != Tokens.ARRAY_END) {
            String value = parseNumber();
            value = value.substring(0, value.length() - 1);
            bytes.add(Byte.parseByte(value));
            if (peek() == Tokens.VALUE_SEPARATOR) {
                consume(); // `,`
            }
            skipEmptyChar(); // skip empty char after `,` or something possible
        }
        consume(); // `]`
        byte[] byteArray = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            byteArray[i] = bytes.get(i);
        }
        byteArrayTag.setValue(byteArray);
        return byteArrayTag;
    }

    private IntArrayTag parseIntArray(String name) {
        IntArrayTag intArrayTag = new IntArrayTag(name);
        consume(3); // `[I;`
        skipEmptyChar(); // skip any possible empty char
        if (peek() == Tokens.ARRAY_END) {
            consume(); // `]`
            return intArrayTag;
        }
        ArrayList<Integer> integers = new ArrayList<>();
        while (peek() != Tokens.ARRAY_END) {
            String value = parseNumber();
            if (value.endsWith("i") || value.endsWith("I")) {
                value = value.substring(0, value.length() - 1);
            }
            integers.add(Integer.parseInt(value));
            if (peek() == Tokens.VALUE_SEPARATOR) {
                consume(); // `,`
            }
            skipEmptyChar(); // skip empty char after `,` or something possible
        }
        consume(); // `]`
        int[] intArray = new int[integers.size()];
        for (int i = 0; i < integers.size(); i++) {
            intArray[i] = integers.get(i);
        }
        intArrayTag.setValue(intArray);
        return intArrayTag;
    }

    private LongArrayTag parseLongArray(String name) {
        LongArrayTag longArrayTag = new LongArrayTag(name);
        consume(3); // `[L;`
        if (peek() == Tokens.ARRAY_END) {
            consume(); // `]`
            return longArrayTag;
        }
        ArrayList<Long> longs = new ArrayList<>();
        while (peek() != Tokens.ARRAY_END) {
            String value = parseNumber();
            value = value.substring(0, value.length() - 1);
            longs.add(Long.parseLong(value));
            if (peek() == Tokens.VALUE_SEPARATOR) {
                consume(); // `,`
            }
            skipEmptyChar(); // skip empty char after `,` or something possible
        }
        consume(); // `]`
        long[] longArray = new long[longs.size()];
        for (int i = 0; i < longs.size(); i++) {
            longArray[i] = longs.get(i);
        }
        longArrayTag.setValue(longArray);
        return longArrayTag;
    }

    public ListTag parseList(String name) {
        ListTag listTag = new ListTag(name);
        consume(); //`[`
        if (peek() == Tokens.ARRAY_END) {
            consume(); // `]`
            return listTag;
        }
        while (peek() != Tokens.ARRAY_END) {
            listTag.add(parseTag(""));
            if (peek() == Tokens.VALUE_SEPARATOR) {
                consume(); // `,`
            }
            skipEmptyChar(); // skip empty char after `,` or something possible
        }
        consume(); // `]`
        return listTag;
    }

    public Tag parsePrimitive(String name) {
        skipEmptyChar();
        char firstChar = peek();
        if (firstChar == Tokens.DOUBLE_QUOTE || firstChar == Tokens.SINGLE_QUOTE) {
            return new StringTag(name, parseQuotedString());
        }
        String value = parseUnquotedString();
        char suffix = Character.toLowerCase(value.charAt(value.length() - 1));
        try {
            if (value.equals("true") || value.equals("false")) {
                return new ByteTag(name, (byte) (value.equals("true") ? 1 : 0));
            }
            if (Tokens.isNumber(suffix)) {
                try {
                    return new IntTag(name, Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    return new DoubleTag(name, Double.parseDouble(value));
                }
            }
            return switch (suffix) {
                case Tokens.TYPE_BYTE -> new ByteTag(name, Byte.parseByte(value.substring(0, value.length() - 1)));
                case Tokens.TYPE_SHORT -> new ShortTag(name, Short.parseShort(value.substring(0, value.length() - 1)));
                case Tokens.TYPE_INT -> new IntTag(name, Integer.parseInt(value.substring(0, value.length() - 1)));
                case Tokens.TYPE_LONG -> new LongTag(name, Long.parseLong(value.substring(0, value.length() - 1)));
                case Tokens.TYPE_FLOAT -> new FloatTag(name, Float.parseFloat(value.substring(0, value.length() - 1)));
                case Tokens.TYPE_DOUBLE ->
                        new DoubleTag(name, Double.parseDouble(value.substring(0, value.length() - 1)));
                default -> new StringTag(name, value); // So this is unquoted string
            };
        } catch (NumberFormatException e) {
            return new StringTag(name, value); // So this is unquoted string
        }
    }

    public String parseString() {
        skipEmptyChar();
        char firstChar = peek();

        if (firstChar == Tokens.DOUBLE_QUOTE || firstChar == Tokens.SINGLE_QUOTE) {
            return parseQuotedString();
        } else {
            return parseUnquotedString();
        }
    }

    public String parseQuotedString() {
        skipEmptyChar();
        boolean isDoubleQuote = consume() == Tokens.DOUBLE_QUOTE; // `"` or `'`
        StringBuilder sb = new StringBuilder();
        boolean escaped = false;

        while (charBuffer.hasRemaining()) {
            char c = charBuffer.get();
            if (escaped) {
                if ((isDoubleQuote && c == Tokens.DOUBLE_QUOTE) || (!isDoubleQuote && c == Tokens.SINGLE_QUOTE)) {
                    sb.append(c);
                    escaped = false;
                    continue;
                }
                sb.append(Tokens.ESCAPE_MARKER).append(c);
                escaped = false;
            } else if (c == Tokens.ESCAPE_MARKER) { // '\'
                escaped = true;
            } else if ((isDoubleQuote && c == Tokens.DOUBLE_QUOTE) || (!isDoubleQuote && c == Tokens.SINGLE_QUOTE)) {
                return sb.toString();
            } else {
                sb.append(c);
            }
        }

        throw new RuntimeException("Unclosed quoted string");
    }

    public String parseUnquotedString() {
        StringBuilder sb = new StringBuilder();
        while (charBuffer.hasRemaining()) {
            skipEmptyChar();
            char c = charBuffer.get();
            if (Tokens.isAllowedInUnquotedString(c)) {
                sb.append(c);
            } else {
                charBuffer.position(charBuffer.position() - 1);
                break;
            }
        }

        String result = sb.toString();
        if (result.isEmpty()) {
            throw new RuntimeException("Expected an unquoted key but found none.");
        }
        return result;
    }

    public String parseNumber() {
        StringBuilder sb = new StringBuilder();
        while (charBuffer.hasRemaining()) {
            char c = charBuffer.get();
            if (Tokens.isNumber(c)) {
                sb.append(c);
            } else if (Tokens.numericType(c)) {
                sb.append(c);
                return sb.toString();
            } else if (Tokens.isFormatChar(c)) {
                continue;
            } else {
                charBuffer.position(charBuffer.position() - 1);
                return sb.toString();
            }
        }
        throw new RuntimeException("Expected an number but found none."); // 这个异常该怎么写？
    }
}
