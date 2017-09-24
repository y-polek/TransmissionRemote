package net.yupol.transmissionremote.app.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.Key;

public class Tracker implements Parcelable {

    @Key public int id;
    @Key public int tier;
    @Key public String announce;
    @Key public String scrape;

    public static final Creator<Tracker> CREATOR = new Creator<Tracker>() {
        @Override
        public Tracker createFromParcel(Parcel in) {
            Tracker tracker = new Tracker();
            tracker.id = in.readInt();
            tracker.tier = in.readInt();
            tracker.announce = in.readString();
            tracker.scrape = in.readString();
            return tracker;
        }

        @Override
        public Tracker[] newArray(int size) {
            return new Tracker[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(tier);
        parcel.writeString(announce);
        parcel.writeString(scrape);
    }
}
