package tech.minediamond.lusternbt.arraylist;

public class IntArrayList {
    private int[] data;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public IntArrayList() {
        this.data = new int[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public void add(int value) {
        ensureCapacity();
        data[size++] = value;
    }

    public int get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return data[index];
    }

    public int[] getIntArray() {
        return data;
    }

    public int size() {
        return size;
    }

    public void clear() {
        data = new int[DEFAULT_CAPACITY];
        size = 0;
    }

    // 动态扩容
    private void ensureCapacity() {
        if (size == data.length) {
            int newCapacity = data.length + (data.length >> 1);
            int[] newData = new int[newCapacity];
            System.arraycopy(data, 0, newData, 0, size);
            data = newData;
        }
    }
}
