package tech.minediamond.lusternbt.SNBT;

import tech.minediamond.lusternbt.tag.builtin.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SNBTWriter {
    private final StringBuilder builder;
    private final boolean linebreak;
    private int depth = 0;

    public SNBTWriter(Tag tag, boolean linebreak) {
        this.builder = new StringBuilder();
        this.linebreak = linebreak;
        stringify(tag);
    }

    public String getSNBTString() {
        return builder.toString();
    }

    public void writeSNBT(Path path) throws IOException {
        Files.writeString(path, getSNBTString());
    }

    private void stringify(Tag tag) {
        if (tag instanceof CompoundTag compoundTag) {
            stringifyCompoundTag(compoundTag);
        } else if (tag instanceof ListTag listTag) {
            stringifyListTag(listTag);
        } else if (tag instanceof StringTag stringTag) {
            stringifyStringTag(stringTag);
        } else if (tag instanceof ByteArrayTag byteArrayTag) {
            stringifyByteArrayTag(byteArrayTag);
        } else if (tag instanceof IntArrayTag intArrayTag) {
            stringifyIntArrayTag(intArrayTag);
        } else if (tag instanceof LongArrayTag longArrayTag) {
            stringifyLongArrayTag(longArrayTag);
        } else if (tag instanceof ByteTag byteTag) {
            builder.append(byteTag.getValue()).append(Tokens.TYPE_BYTE);
        } else if (tag instanceof ShortTag shortTag) {
            builder.append(shortTag.getValue()).append(Tokens.TYPE_SHORT);
        } else if (tag instanceof IntTag intTag) {
            builder.append(intTag.getValue());
        } else if (tag instanceof LongTag longTag) {
            builder.append(longTag.getValue()).append(Tokens.TYPE_LONG_UPPER);
        } else if (tag instanceof FloatTag floatTag) {
            builder.append(floatTag.getValue()).append(Tokens.TYPE_FLOAT);
        } else if (tag instanceof DoubleTag doubleTag) {
            builder.append(doubleTag.getValue()).append(Tokens.TYPE_DOUBLE);
        }
    }

    private void stringifyCompoundTag(CompoundTag compoundTag) {
        if (linebreak) {
            stringifyCompoundTagIfLineWarp(compoundTag);
        } else  {
            stringifyCompoundTagIfLineNotWarp(compoundTag);
        }
    }

    private void stringifyCompoundTagIfLineWarp(CompoundTag compoundTag) {
        builder.append(Tokens.COMPOUND_BEGIN);
        if (!compoundTag.isEmpty()) {
            depth++;
            boolean isFirst = true;
            for (Tag subTag : compoundTag) {
                if (!isFirst) {
                    builder.append(Tokens.VALUE_SEPARATOR);
                } else {
                    isFirst = false;
                }
                builder.append(Tokens.NEWLINE);
                addTab();
                setCompoundItemString(subTag);
            }
            builder.append(Tokens.NEWLINE);
            depth--;
            addTab();
        }
        builder.append(Tokens.COMPOUND_END);
    }

    private void stringifyCompoundTagIfLineNotWarp(CompoundTag compoundTag) {
        builder.append(Tokens.COMPOUND_BEGIN);
        if (!compoundTag.isEmpty()) {
            boolean isFirst = true;
            for (Tag subTag : compoundTag) {
                if (!isFirst) {
                    builder.append(Tokens.VALUE_SEPARATOR);
                    builder.append(Tokens.SPACE);
                } else {
                    isFirst = false;
                }
                setCompoundItemString(subTag);
            }
        }
        builder.append(Tokens.COMPOUND_END);
    }

    private void setCompoundItemString(Tag subTag) {
        boolean needQuotation = Tokens.needQuotation(subTag.getName());
        if (needQuotation) {
            builder.append(Tokens.DOUBLE_QUOTE);
        }
        builder.append(subTag.getName());
        if (needQuotation) {
            builder.append(Tokens.DOUBLE_QUOTE);
        }
        builder.append(Tokens.COMPOUND_KEY_VALUE_SEPARATOR)
                .append(Tokens.SPACE);
        stringify(subTag);
    }

    private void stringifyListTag(ListTag listTag) {
        if (linebreak) {
            stringifyListTagIfLineWarp(listTag);
        } else {
            stringifyListTagIfLineNotWarp(listTag);
        }
    }

    private void stringifyListTagIfLineWarp(ListTag listTag) {
        builder.append(Tokens.ARRAY_BEGIN);
        if (listTag.size() != 0) {
            depth++;
            boolean isFirst = true;
            for (Tag subTag : listTag) {
                if (!isFirst) {
                    builder.append(Tokens.VALUE_SEPARATOR);
                } else {
                    isFirst = false;
                }
                builder.append(Tokens.NEWLINE);
                addTab();
                stringify(subTag);
            }
            builder.append(Tokens.NEWLINE);
            depth--;
            addTab();
        }
        builder.append(Tokens.ARRAY_END);
    }

    private void stringifyListTagIfLineNotWarp(ListTag listTag) {
        builder.append(Tokens.ARRAY_BEGIN);
        if (listTag.size() != 0) {
            boolean isFirst = true;
            for (Tag subTag : listTag) {
                if (!isFirst) {
                    builder.append(Tokens.VALUE_SEPARATOR);
                    builder.append(Tokens.SPACE);
                } else  {
                    isFirst = false;
                }
                stringify(subTag);
            }
        }
        builder.append(Tokens.ARRAY_END);
    }

    private void stringifyStringTag(StringTag stringTag) {
        builder.append(Tokens.DOUBLE_QUOTE).append(stringTag.getValue().replace("\"", "\\\"")).append(Tokens.DOUBLE_QUOTE);
    }

    private void stringifyByteArrayTag(ByteArrayTag byteArrayTag) {
        builder.append(Tokens.ARRAY_BEGIN)
                .append(Tokens.TYPE_BYTE_UPPER)
                .append(Tokens.ARRAY_SIGNATURE_SEPARATOR);
        if (byteArrayTag.getValue().length != 0) {
            for (byte b : byteArrayTag.getValue()) {
                builder.append(Tokens.SPACE)
                        .append(b)
                        .append(Tokens.TYPE_BYTE)
                        .append(Tokens.VALUE_SEPARATOR);
            }
            builder.setLength(builder.length() - 1);
        }
        builder.append(Tokens.ARRAY_END);
    }

    private void stringifyIntArrayTag(IntArrayTag intArrayTag) {
        builder.append(Tokens.ARRAY_BEGIN)
                .append(Tokens.TYPE_INT_UPPER)
                .append(Tokens.ARRAY_SIGNATURE_SEPARATOR);
        if (intArrayTag.getValue().length != 0) {
            for (int i : intArrayTag.getValue()) {
                builder.append(Tokens.SPACE)
                        .append(i)
                        .append(Tokens.VALUE_SEPARATOR);
            }
            builder.setLength(builder.length() - 1);
        }
        builder.append(Tokens.ARRAY_END);
    }

    private void stringifyLongArrayTag(LongArrayTag longArrayTag) {
        builder.append(Tokens.ARRAY_BEGIN)
                .append(Tokens.TYPE_LONG_UPPER)
                .append(Tokens.ARRAY_SIGNATURE_SEPARATOR);
        if (longArrayTag.getValue().length != 0) {
            for (long l : longArrayTag.getValue()) {
                builder.append(Tokens.SPACE)
                        .append(l)
                        .append(Tokens.TYPE_LONG_UPPER)
                        .append(Tokens.VALUE_SEPARATOR);
            }
            builder.setLength(builder.length() - 1);
        }
        builder.append(Tokens.ARRAY_END);
    }

    private void addTab() {
        int i = 0;
        while (i < depth) {
            builder.append(Tokens.TAB);
            i++;
        }
    }
}
