package net.yupol.transmissionremote.app.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.Key;

public class Peer implements Parcelable {

    @Key public String address;
    @Key public String clientName;
    @Key public boolean clientIsChoked;
    @Key public boolean clientIsInterested;
    @Key public String flagStr;
    @Key public boolean isDownloadingFrom;
    @Key public boolean isEncrypted;
    @Key public boolean isIncoming;
    @Key public boolean isUploadingTo;
    @Key public boolean isUTP;
    @Key public boolean peerIsChoked;
    @Key public boolean peerIsInterested;
    @Key public int port;
    @Key public double progress;
    @Key public long rateToClient;
    @Key public long rateToPeer;

    public Peer() {}

    protected Peer(Parcel in) {
        address = in.readString();
        clientName = in.readString();
        clientIsChoked = in.readByte() != 0;
        clientIsInterested = in.readByte() != 0;
        flagStr = in.readString();
        isDownloadingFrom = in.readByte() != 0;
        isEncrypted = in.readByte() != 0;
        isIncoming = in.readByte() != 0;
        isUploadingTo = in.readByte() != 0;
        isUTP = in.readByte() != 0;
        peerIsChoked = in.readByte() != 0;
        peerIsInterested = in.readByte() != 0;
        port = in.readInt();
        progress = in.readDouble();
        rateToClient = in.readLong();
        rateToPeer = in.readLong();
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {
        @Override
        public Peer createFromParcel(Parcel in) {
            return new Peer(in);
        }

        @Override
        public Peer[] newArray(int size) {
            return new Peer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(address);
        parcel.writeString(clientName);
        parcel.writeByte((byte) (clientIsChoked ? 1 : 0));
        parcel.writeByte((byte) (clientIsInterested ? 1 : 0));
        parcel.writeString(flagStr);
        parcel.writeByte((byte) (isDownloadingFrom ? 1 : 0));
        parcel.writeByte((byte) (isEncrypted ? 1 : 0));
        parcel.writeByte((byte) (isIncoming ? 1 : 0));
        parcel.writeByte((byte) (isUploadingTo ? 1 : 0));
        parcel.writeByte((byte) (isUTP ? 1 : 0));
        parcel.writeByte((byte) (peerIsChoked ? 1 : 0));
        parcel.writeByte((byte) (peerIsInterested ? 1 : 0));
        parcel.writeInt(port);
        parcel.writeDouble(progress);
        parcel.writeLong(rateToClient);
        parcel.writeLong(rateToPeer);
    }
}
