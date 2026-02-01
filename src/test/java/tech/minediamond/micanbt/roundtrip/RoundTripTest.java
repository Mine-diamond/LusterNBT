package tech.minediamond.micanbt.roundtrip;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tech.minediamond.micanbt.NBT.NBT;
import tech.minediamond.micanbt.SNBT.SNBT;
import tech.minediamond.micanbt.SNBT.SNBTStyle;
import tech.minediamond.micanbt.tag.*;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class RoundTripTest {

    @TempDir
    Path tempDir;

    @Test
    public void testSNBTRoundTrip() {
        Tag tag = buildTag();
        Tag newTag = SNBT.parse(SNBT.stringify(tag, true, SNBTStyle.COMPACT));
        assertInstanceOf(CompoundTag.class, newTag);

        assertEquals(tag, newTag);
    }

    @Test
    public void testNBTRoundTrip() throws IOException {
        Path filePath = tempDir.resolve("test.nbt");

        CompoundTag tag = buildTag();
        NBT.write(tag, filePath);
        CompoundTag read = NBT.read(filePath);
        assertInstanceOf(CompoundTag.class, read);

        assertEquals(tag, read);
    }

    public static CompoundTag buildTag() {
        CompoundTag tag = new CompoundTag("tag");
        tag.put(new ByteArrayTag("ByteArrayTag", new byte[]{1, 0, 3}));
        tag.put(new ByteTag("ByteTag", (byte) 2));
        tag.put(new DoubleTag("DoubleTag", 1.0));
        tag.put(new FloatTag("FloatTag", 1.0f));
        tag.put(new IntArrayTag("IntArrayTag", new int[]{1, 2, 3}));
        tag.put(new IntTag("IntTag", 1));
        tag.put(new LongArrayTag("LongArrayTag", new long[]{1, 2, 3}));
        tag.put(new LongTag("LongTag", 1L));
        tag.put(new ShortTag("ShortTag", (short) 2));
        tag.put(new StringTag("StringTag", "str"));

        CompoundTag subCompoundTag = new CompoundTag("subCompoundTag");
        subCompoundTag.put(new ByteArrayTag("ByteArrayTag", new byte[]{1, 0, 3}));
        subCompoundTag.put(new ByteTag("ByteTag", (byte) 2));

        ListTag<StringTag> listWithItem = new ListTag<>("ListWithItem");
        listWithItem.add(new StringTag("", "str"));
        listWithItem.add(new StringTag("", "str1"));
        listWithItem.add(new StringTag("", "str2"));

        tag.put(new CompoundTag("subEmptyCompoundTag"));
        tag.put(subCompoundTag);
        tag.put(listWithItem);
        tag.put(new StringTag("Name With Space", "add a \" here "));
        tag.put(new IntArrayTag("EmptyIntArrayTag", new int[]{}));

        return tag;
    }
}
