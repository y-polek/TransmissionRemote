package net.yupol.transmissionremote.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import net.yupol.transmissionremote.data.api.model.PeerEntity;

public class Peer implements Parcelable {

    @Json(name = "address") public String address;
    @Json(name = "clientName") public String clientName;
    @Json(name = "clientIsChoked") public boolean clientIsChoked;
    @Json(name = "clientIsInterested") public boolean clientIsInterested;
    @Json(name = "flagStr") public String flagStr;
    @Json(name = "isDownloadingFrom") public boolean isDownloadingFrom;
    @Json(name = "isEncrypted") public boolean isEncrypted;
    @Json(name = "isIncoming") public boolean isIncoming;
    @Json(name = "isUploadingTo") public boolean isUploadingTo;
    @Json(name = "isUTP") public boolean isUTP;
    @Json(name = "peerIsChoked") public boolean peerIsChoked;
    @Json(name = "peerIsInterested") public boolean peerIsInterested;
    @Json(name = "port") public int port;
    @Json(name = "progress") public double progress;
    @Json(name = "rateToClient") public long rateToClient;
    @Json(name = "rateToPeer") public long rateToPeer;

    public Peer() {
        // required by GSON
    }

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

    public static Peer fromEntity(PeerEntity entity) {
        Peer peer = new Peer();
        peer.address = entity.address;
        peer.clientName = entity.clientName;
        peer.clientIsChoked = entity.clientIsChoked;
        peer.clientIsInterested = entity.clientIsInterested;
        peer.flagStr = entity.flagStr;
        peer.isDownloadingFrom = entity.isDownloadingFrom;
        peer.isEncrypted = entity.isEncrypted;
        peer.isIncoming = entity.isIncoming;
        peer.isUploadingTo = entity.isUploadingTo;
        peer.isUTP = entity.isUTP;
        peer.peerIsChoked = entity.peerIsChoked;
        peer.peerIsInterested = entity.peerIsInterested;
        peer.port = entity.port;
        peer.progress = entity.progress;
        peer.rateToClient = entity.rateToClient;
        peer.rateToPeer = entity.rateToPeer;
        return peer;
    }
}
