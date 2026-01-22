package tech.minediamond.lusternbt.arraylist;

public class ByteArrayList {
    private byte[] data;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public ByteArrayList() {
        data = new byte[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public void add(byte value) {
        ensureCapacity();
        data[size++] = value;
    }

    public byte get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return data[index];
    }

    public byte[] getBytesArray() {
        return data;
    }

    public int size() {
        return size;
    }

    public void clear() {
        data = new byte[DEFAULT_CAPACITY];
        size = 0;
    }

    private void ensureCapacity() {
        if (size == data.length) {
            int newCapacity = data.length + (data.length >> 1);
            byte[] newData = new byte[newCapacity];
            System.arraycopy(data, 0, newData, 0, size);
            data = newData;
        }
    }
}
