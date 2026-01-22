package tech.minediamond.lusternbt.arraylist;

public class LongArrayList {
    private long[] data;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public LongArrayList() {
        data = new long[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public void add(long value) {
        ensureCapacity();
        data[size++] = value;
    }

    public long get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return data[index];
    }

    public long[] getLongArray() {
        return data;
    }

    public int size() {
        return size;
    }

    public void clear() {
        data = new long[DEFAULT_CAPACITY];
        size = 0;
    }

    private void ensureCapacity() {
        if (size == data.length) {
            int newCapacity = data.length + (data.length >> 1);
            long[] newData = new long[newCapacity];
            System.arraycopy(data, 0, newData, 0, size);
            data = newData;
        }
    }
}
