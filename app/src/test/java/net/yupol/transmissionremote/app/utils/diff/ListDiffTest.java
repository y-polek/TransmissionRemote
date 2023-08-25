package net.yupol.transmissionremote.app.utils.diff;

import static com.google.common.truth.Truth.assertThat;

import androidx.annotation.NonNull;

import net.yupol.transmissionremote.app.model.ID;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListDiffTest {

    private List<Value> initialList;
    private List<Value> nonStructuralChangesList;
    private List<Value> insertsOnlyList;
    private List<Value> removesOnlyList;
    private List<Value> allStructuralChangesList;
    private List<Value> changedSecondValue;

    @Before
    public void setUp() {
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

    @Test
    public void testNoChanges() {
        ListDiff<Value> diff = new ListDiff<>(initialList, Value.deepCopyOf(initialList));

        assertThat(diff.containStructuralChanges()).isFalse();
        assertThat(diff.getChangedItems()).isEmpty();
    }

    @Test
    public void testNoStructuralChanges() {
        ListDiff<Value> diff = new ListDiff<>(initialList, nonStructuralChangesList);
        List<Range> changedItems = diff.getChangedItems();

        assertThat(diff.containStructuralChanges()).isFalse();

        assertThat(changedItems).hasSize(3); // 3 changed ranges
        // Range 1:
        Range range = changedItems.get(0);
        assertThat(range.start).isEqualTo(1); // starts at index 1,
        assertThat(range.count).isEqualTo(1); // contain 1 item

        // Range 2:
        range = changedItems.get(1);
        assertThat(range.start).isEqualTo(3); // starts at index 3,
        assertThat(range.count).isEqualTo(3); // contain 3 item

        // Range 3:
        range = changedItems.get(2);
        assertThat(range.start).isEqualTo(9); // starts at index 9,
        assertThat(range.count).isEqualTo(1); // contain 1 item
    }

    @Test
    public void testStructuralChanges() {
        assertThat(new ListDiff<>(initialList, insertsOnlyList).containStructuralChanges()).isTrue();
        assertThat(new ListDiff<>(initialList, removesOnlyList).containStructuralChanges()).isTrue();
        assertThat(new ListDiff<>(initialList, allStructuralChangesList).containStructuralChanges()).isTrue();
    }

    @Test
    public void testGetChangedItemsReturnNullIfStructuralChanges() {
        ListDiff<Value> diff = new ListDiff<>(initialList, allStructuralChangesList);

        assertThat(diff.containStructuralChanges()).isTrue();
        assertThat(diff.getChangedItems()).isNull();
    }

    @Test
    public void testGetChangedItemsWithoutCallingContainStructuralChanges() {
        ListDiff<Value> diff = new ListDiff<>(initialList, nonStructuralChangesList);

        assertThat(diff.getChangedItems()).hasSize(3);
    }

    @Test
    public void testEmptyLists() {
        assertThat(new ListDiff<>(initialList, Collections.emptyList()).containStructuralChanges()).isTrue();
        assertThat(new ListDiff<>(Collections.emptyList(), initialList).containStructuralChanges()).isTrue();
        assertThat(new ListDiff<>(Collections.emptyList(), Collections.emptyList()).containStructuralChanges()).isFalse();
    }

    @Test
    public void testDefaultEqualsImpl() {
        ListDiff<Value> diff = new ListDiff<>(initialList, changedSecondValue);

        assertThat(diff.getChangedItems()).hasSize(1);
    }

    @Test
    public void testCustomEqualsImpl() {
        Equals<Value> ignoreSecondValueEquals = (o1, o2) -> {
            if (o1 == null) return o2 == null;
            return o1.id == o2.id && o1.value == o2.value;
        };
        ListDiff<Value> diff = new ListDiff<>(initialList, changedSecondValue, ignoreSecondValueEquals);

        assertThat(diff.getChangedItems()).isEmpty();
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
        @NonNull
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
