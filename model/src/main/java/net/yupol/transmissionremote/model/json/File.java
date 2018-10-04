package net.yupol.transmissionremote.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

public class File implements Parcelable {
    @Json(name = "name") private String path;
    transient private String name;
    @Json(name = "length") private long length;

    public File(String path, long length) {
        this.path = path;
        this.length = length;
    }

    public File(Parcel in) {
        path = in.readString();
        length = in.readLong();
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        if (name == null) {
            String[] nameParts = path.split("/");
            name = nameParts[nameParts.length - 1];
        }
        return name;
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
        out.writeString(path);
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
