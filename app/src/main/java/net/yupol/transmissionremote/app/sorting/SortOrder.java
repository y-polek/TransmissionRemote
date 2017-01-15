package net.yupol.transmissionremote.app.sorting;

import java.util.Collections;
import java.util.Comparator;

public enum SortOrder {
    ASCENDING("\u25B2"),
    DESCENDING("\u25BC");

    private String symbol;

    SortOrder(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public <T> Comparator<T> comparator(Comparator<T> comparator) {
        return this == ASCENDING ? comparator : Collections.reverseOrder(comparator);
    }

    public SortOrder reversed() {
        return this == ASCENDING ? DESCENDING : ASCENDING;
    }
}
