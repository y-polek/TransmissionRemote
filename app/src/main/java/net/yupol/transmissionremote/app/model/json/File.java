package net.yupol.transmissionremote.app.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.Key;

public class File implements Parcelable {
    @Key private String name;
    @Key private long bytesCompleted;
    @Key private long length;

    public File() {}

    public File(Parcel in) {
        name = in.readString();
        bytesCompleted = in.readLong();
        length = in.readLong();
    }

    public String getName() {
        return name;
    }

    public long getBytesCompleted() {
        return bytesCompleted;
    }

    public long getLength() {
        return length;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeLong(bytesCompleted);
        out.writeLong(length);
    }

    public static final Creator<File> CREATOR = new Creator<File>() {
        @Override
        public File createFromParcel(Parcel in) {
            return new File(in);
        }

        @Override
        public File[] newArray(int size) {
            return new File[size];
        }
    };
}
