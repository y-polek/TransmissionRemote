package net.yupol.transmissionremote.app.sorting;

import androidx.annotation.StringRes;

import com.google.common.base.Strings;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.model.json.Peer;

import java.util.Comparator;

public enum PeersSortedBy {

    ADDRESS(R.string.peers_sort_by_address, new Comparator<Peer>() {
        @Override
        public int compare(Peer p1, Peer p2) {
            String address1 = Strings.nullToEmpty(p1.address);
            String address2 = Strings.nullToEmpty(p2.address);
            return address1.compareToIgnoreCase(address2);
        }
    }),

    CLIENT(R.string.peers_sort_by_client, new Comparator<Peer>() {
        @Override
        public int compare(Peer p1, Peer p2) {
            String client1 = Strings.nullToEmpty(p1.clientName);
            String client2 = Strings.nullToEmpty(p2.clientName);
            return client1.compareToIgnoreCase(client2);
        }
    }),

    PROGRESS(R.string.peers_sort_by_progress, new Comparator<Peer>() {
        @Override
        public int compare(Peer p1, Peer p2) {
            return Double.compare(p1.progress, p2.progress);
        }
    }),

    DOWNLOADED_RATE(R.string.peers_sort_by_download_rate, new Comparator<Peer>() {
        @Override
        public int compare(Peer p1, Peer p2) {
            return Long.signum(p2.rateToClient - p1.rateToClient);
        }
    }),

    UPLOAD_RATE(R.string.peers_sort_by_upload_rate, new Comparator<Peer>() {
        @Override
        public int compare(Peer p1, Peer p2) {
            return Long.signum(p2.rateToPeer - p1.rateToPeer);
        }
    });

    @StringRes public final int nameResId;
    public final Comparator<Peer> comparator;

    PeersSortedBy(@StringRes int nameResId, Comparator<Peer> comparator) {
        this.nameResId = nameResId;
        this.comparator = comparator;
    }
}
