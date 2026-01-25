package tech.minediamond.micanbt.conversion.builtin;

import tech.minediamond.micanbt.conversion.TagConverter;
import tech.minediamond.micanbt.tag.builtin.ByteArrayTag;

/**
 * A converter that converts between ByteArrayTag and byte[].
 */
public class ByteArrayTagConverter implements TagConverter<ByteArrayTag, byte[]> {
    @Override
    public byte[] convert(ByteArrayTag tag) {
        return tag.getValue();
    }

    @Override
    public ByteArrayTag convert(String name, byte[] value) {
        return new ByteArrayTag(name, value);
    }
}
