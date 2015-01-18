package net.yupol.transmissionremote.app.model.json;

import com.google.api.client.util.Key;

import java.util.AbstractList;

public class Torrents extends AbstractList<Torrent> {

    /*
     * Default constructor required for instantiating by JSON parsing tool
     */
    public Torrents() {}

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
    public int size() {
        return torrents.length;
    }
}
