package tech.minediamond.micanbt.NBT;

import tech.minediamond.micanbt.tag.builtin.CompoundTag;
import tech.minediamond.micanbt.tag.builtin.Tag;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class NBT {

    public static CompoundTag read(Path path) throws IOException {
        return NBTIO.read(path);
    }

    public static CompoundTag read(Path path, boolean compressed, boolean littleEndian) throws IOException {
        return NBTIO.read(path, compressed, littleEndian);
    }

    public static Tag prase(InputStream in) throws IOException {
        return NBTIO.readTag(in);
    }

    public static Tag prase(InputStream in, boolean littleEndian) throws IOException {
        return NBTIO.readTag(in, littleEndian);
    }

    public static Tag prase(DataInput in) throws IOException {
        return NBTIO.readTag(in);
    }

    public static Tag readAnyTag(InputStream in) throws IOException {
        return NBTIO.readAnyTag(in);
    }

    public static Tag readAnyTag(InputStream in, boolean littleEndian) throws IOException {
        return NBTIO.readAnyTag(in, littleEndian);
    }

    public static Tag readAnyTag(DataInput in) throws IOException {
        return NBTIO.readAnyTag(in);
    }

    public static void write(CompoundTag tag, Path path) throws IOException {
        NBTIO.writeFile(tag, path);
    }

    public static void write(CompoundTag tag, Path path, boolean compressed, boolean littleEndian) throws IOException {
        NBTIO.writeFile(tag, path, compressed, littleEndian);
    }
}
