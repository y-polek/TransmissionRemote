package net.yupol.transmissionremote.app.utils.diff;

import net.yupol.transmissionremote.model.ID;

import java.util.LinkedList;
import java.util.List;

public class ListDiff<T extends ID> {

    private List<T> oldList, newList;
    private Equals equalsImpl;

    private Boolean containStructuralChanges = null;
    private List<Range> changedItems;

    public ListDiff(List<T> oldList, List<T> newList) {
        this(oldList, newList, new Equals() {
            @Override
            public boolean equals(Object o1, Object o2) {
                if (o1 == null) return o2 == null;
                return o1.equals(o2);
            }
        });
    }

    public ListDiff(List<T> oldList, List<T> newList, Equals equalsImpl) {
        this.oldList = oldList;
        this.newList = newList;
        this.equalsImpl = equalsImpl;
    }

    public boolean containStructuralChanges() {
        if (containStructuralChanges == null) {
            containStructuralChanges = computeChanges();
        }

        return containStructuralChanges;
    }

    /**
     * @return non-structural changes.
     * Empty list if there is no change.
     * {@code null} if diff contain structural changes.
     */
    public List<Range> getChangedItems() {
        if (containStructuralChanges()) return null;
        return changedItems;
    }

    private boolean computeChanges() {
        if (oldList.size() != newList.size()) {
            return true;
        }

        int rangeStart = -1;
        int rangeSize = 0;
        List<Range> ranges = new LinkedList<>();
        for (int i=0; i<oldList.size(); i++) {
            T oldListItem = oldList.get(i);
            T newListItem = newList.get(i);

            if (equalsImpl.equals(oldListItem, newListItem)) {
                if (rangeSize > 0) {
                    ranges.add(new Range(rangeStart, rangeSize));
                    rangeSize = 0;
                }
            } else if (oldListItem.getId() == newListItem.getId()) {
                if (rangeSize == 0) rangeStart = i;
                rangeSize++;
            } else {
                return true;
            }
        }
        if (rangeSize > 0) {
            ranges.add(new Range(rangeStart, rangeSize));
        }

        changedItems = ranges;

        return false;
    }
}
