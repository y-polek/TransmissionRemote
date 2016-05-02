package net.yupol.transmissionremote.app.model.json;

import com.google.api.client.util.Key;

import java.util.AbstractList;
import java.util.Arrays;

public class Torrents extends AbstractList<Torrent> {

    private int size = -1;

    /*
     * Default constructor required for instantiating by JSON parsing tool
     */
    public Torrents() {
        this.torrents = new Torrent[0];
    }

    public Torrents(Torrent[] torrents) {
        this.torrents = torrents;
    }

    @Key
    private Torrent[] torrents;

    @Override
    public Torrent get(int location) {
        if (location < 0 || location >= size())
            throw new IndexOutOfBoundsException("Invalid index " + location + ", size is " + size());
        return torrents[location];
    }

    @Override
    public void add(int location, Torrent object) {
        int s = size();
        if (location < 0 || location > s)
            throw new IndexOutOfBoundsException("Invalid index " + location + ", size is " + s);

        if (location >= size) size = location + 1;
        if (size > torrents.length) {
            int newSize = size == 0 ? 8 : size * 2;
            torrents = Arrays.copyOf(torrents, newSize);
        }

        torrents[location] = object;
    }

    @Override
    public int size() {
        if (size > 0) return size;
        return torrents.length;
    }
}
