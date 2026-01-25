package tech.minediamond.micanbt.conversion.builtin;

import tech.minediamond.micanbt.conversion.TagConverter;
import tech.minediamond.micanbt.tag.builtin.IntTag;

/**
 * A converter that converts between IntTag and int.
 */
public class IntTagConverter implements TagConverter<IntTag, Integer> {
    @Override
    public Integer convert(IntTag tag) {
        return tag.getValue();
    }

    @Override
    public IntTag convert(String name, Integer value) {
        return new IntTag(name, value);
    }
}
