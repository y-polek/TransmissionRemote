package net.yupol.transmissionremote.app.utils.diff;

import junit.framework.TestCase;

import net.yupol.transmissionremote.model.ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListDiffTest extends TestCase {

    private List<Value> initialList;
    private List<Value> nonStructuralChangesList;
    private List<Value> insertsOnlyList;
    private List<Value> removesOnlyList;
    private List<Value> allStructuralChangesList;
    private List<Value> changedSecondValue;

    public void setUp() throws Exception {
        initialList = Arrays.asList(
                new Value(0, 0),
                new Value(1, 1),
                new Value(2, 2),
                new Value(3, 3),
                new Value(4, 4),
                new Value(5, 5),
                new Value(6, 6),
                new Value(7, 7),
                new Value(8, 8),
                new Value(9, 9)
        );

        // Contain no structural changes, only item changes
        nonStructuralChangesList = Value.deepCopyOf(initialList);
        // ----- Range 1; 1 item -----
        nonStructuralChangesList.get(1).value += 10;
        // ----- Range 2; 3 items -----
        nonStructuralChangesList.get(3).value += 10;
        nonStructuralChangesList.get(4).value += 10;
        nonStructuralChangesList.get(5).value += 10;
        // ----- Range 3; 1 item -----
        nonStructuralChangesList.get(9).value += 10;

        insertsOnlyList = Value.deepCopyOf(initialList);
        insertsOnlyList.add(5, new Value(10, 10));

        removesOnlyList = Value.deepCopyOf(initialList);
        removesOnlyList.remove(7);

        allStructuralChangesList = Value.deepCopyOf(initialList);
        allStructuralChangesList.add(8, new Value(11, 11));
        allStructuralChangesList.remove(0);

        changedSecondValue = Value.deepCopyOf(initialList);
        changedSecondValue.get(0).value2 += 10;
    }

    public void testNoChanges() {
        ListDiff<Value> diff = new ListDiff<>(initialList, Value.deepCopyOf(initialList));

        assertFalse(diff.containStructuralChanges());
        assertEquals(0, diff.getChangedItems().size());
    }

    public void testNoStructuralChanges() {
        ListDiff<Value> diff = new ListDiff<>(initialList, nonStructuralChangesList);
        List<Range> changedItems = diff.getChangedItems();

        assertFalse(diff.containStructuralChanges());

        assertEquals(3, changedItems.size()); // 3 changed ranges
        // Range 1:
        Range range = changedItems.get(0);
        assertEquals(1, range.start); // starts at index 1,
        assertEquals(1, range.count); // contain 1 item

        // Range 2:
        range = changedItems.get(1);
        assertEquals(3, range.start); // starts at index 3,
        assertEquals(3, range.count); // contain 3 item

        // Range 3:
        range = changedItems.get(2);
        assertEquals(9, range.start); // starts at index 9,
        assertEquals(1, range.count); // contain 1 item
    }

    public void testStructuralChanges() {

        assertTrue(new ListDiff<>(initialList, insertsOnlyList).containStructuralChanges());
        assertTrue(new ListDiff<>(initialList, removesOnlyList).containStructuralChanges());
        assertTrue(new ListDiff<>(initialList, allStructuralChangesList).containStructuralChanges());
    }

    public void testGetChangedItemsReturnNullIfStructuralChanges() {
        ListDiff<Value> diff = new ListDiff<>(initialList, allStructuralChangesList);

        assertTrue(diff.containStructuralChanges());
        assertNull(diff.getChangedItems());
    }

    public void testGetChangedItemsWithoutCallingContainStructuralChanges() {
        ListDiff<Value> diff = new ListDiff<>(initialList, nonStructuralChangesList);

        assertEquals(3, diff.getChangedItems().size());
    }

    public void testEmptyLists() {
        assertTrue(new ListDiff<>(initialList, Collections.<Value>emptyList()).containStructuralChanges());
        assertTrue(new ListDiff<>(Collections.<Value>emptyList(), initialList).containStructuralChanges());
        assertFalse(new ListDiff<>(Collections.<ID>emptyList(), Collections.<ID>emptyList()).containStructuralChanges());
    }

    public void testDefaultEqualsImpl() {
        ListDiff<Value> diff = new ListDiff<>(initialList, changedSecondValue);

        assertEquals(1, diff.getChangedItems().size());
    }

    public void testCustomEqualsImpl() {
        Equals<Value> ignoreSecondValueEquals = new Equals<Value>() {
            @Override
            public boolean equals(Value o1, Value o2) {
                if (o1 == null) return o2 == null;
                return o1.id == o2.id && o1.value == o2.value;
            }
        };
        ListDiff<Value> diff = new ListDiff<>(initialList, changedSecondValue, ignoreSecondValueEquals);

        assertEquals(0, diff.getChangedItems().size());
    }

    private static class Value implements ID {

        public int id;
        public int value;
        public int value2;

        public Value(int id, int value) {
            this.id = id;
            this.value = value;
        }

        public int getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Value value1 = (Value) o;

            if (id != value1.id) return false;
            if (value != value1.value) return false;
            return value2 == value1.value2;

        }

        @Override
        public String toString() {
            return "Value{" +
                    "id=" + id +
                    ", value=" + value +
                    '}';
        }

        public static List<Value> deepCopyOf(List<Value> list) {
            List<Value> newList = new ArrayList<>(list.size());
            for (Value value : list) {
                newList.add(new Value(value.id, value.value));
            }
            return newList;
        }
    }
}
