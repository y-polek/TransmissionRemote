package net.yupol.transmissionremote.app.sorting;

import com.google.common.base.Optional;
import com.google.common.collect.ComparisonChain;
import com.google.common.primitives.Ints;

import net.yupol.transmissionremote.app.model.json.Torrent;

import java.util.Comparator;

public enum SortedBy {

    NAME(new Comparator<Torrent>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            String name1 = Optional.fromNullable(t1.getName()).or("");
            String name2 = Optional.fromNullable(t2.getName()).or("");
            return name1.compareToIgnoreCase(name2);
        }
    }),

    SIZE(new Comparator<Torrent>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return Long.signum(t2.getTotalSize() - t1.getTotalSize());
        }
    }),

    TIME_REMAINING(new Comparator<Torrent>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return ComparisonChain.start()
                    .compareFalseFirst(t1.isCompleted(), t2.isCompleted())
                    .compareFalseFirst(t1.getEta() < 0, t2.getEta() < 0)
                    .compare(t1.getEta(), t2.getEta())
                    .result();
        }
    }),

    DATE_ADDED(new Comparator<Torrent>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return Long.signum(t2.getAddedDate() - t1.getAddedDate());
        }
    }),

    PROGRESS(new Comparator<Torrent>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return Double.compare(t2.getPercentDone(), t1.getPercentDone());
        }
    }),

    QUEUE_POSITION(new Comparator<Torrent>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return Ints.compare(t1.getQueuePosition(), t2.getQueuePosition());
        }
    }),

    UPLOAD_RATIO(new Comparator<Torrent>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return Double.compare(t1.getUploadRatio(), t2.getUploadRatio());
        }
    }),

    ACTIVITY(new Comparator<Torrent>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return ComparisonChain.start()
                    .compare(t2.getActivity(), t1.getActivity())
                    .compare(t2.getStatus().value, t1.getStatus().value)
                    .compare(t1.getQueuePosition(), t2.getQueuePosition())
                    .result();
        }
    }),

    STATE(new Comparator<Torrent>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return ComparisonChain.start()
                    .compare(t2.getStatus().value, t1.getStatus().value)
                    .compare(t1.getQueuePosition(), t2.getQueuePosition())
                    .result();
        }
    });

    private Comparator<Torrent> comparator;

    SortedBy(Comparator<Torrent> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Torrent> getComparator() {
        return comparator;
    }
}
