package tech.minediamond.micanbt.conversion.builtin;

import tech.minediamond.micanbt.conversion.TagConverter;
import tech.minediamond.micanbt.tag.builtin.ShortTag;

/**
 * A converter that converts between ShortTag and short.
 */
public class ShortTagConverter implements TagConverter<ShortTag, Short> {
    @Override
    public Short convert(ShortTag tag) {
        return tag.getValue();
    }

    @Override
    public ShortTag convert(String name, Short value) {
        return new ShortTag(name, value);
    }
}
