package net.yupol.transmissionremote.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

public class FileStat implements Parcelable {
    @Json(name = "wanted") private boolean wanted;
    @Json(name = "priority") private int priority;
    @Json(name = "bytesCompleted") private long bytesCompleted;

    public FileStat(boolean wanted, int priority, long bytesCompleted) {
        this.wanted = wanted;
        this.priority = priority;
        this.bytesCompleted = bytesCompleted;
    }

    public FileStat(Parcel in) {
        wanted = in.readInt() != 0;
        priority = in.readInt();
        bytesCompleted = in.readLong();
    }

    public boolean isWanted() {
        return wanted;
    }

    public void setWanted(boolean wanted) {
        this.wanted = wanted;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getBytesCompleted() {
        return bytesCompleted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(wanted ? 1 : 0);
        out.writeInt(priority);
        out.writeLong(bytesCompleted);
    }

    public static final Creator<FileStat> CREATOR = new Creator<FileStat>() {
        @Override
        public FileStat createFromParcel(Parcel in) {
            return new FileStat(in);
        }

        @Override
        public FileStat[] newArray(int size) {
            return new FileStat[size];
        }
    };
}
