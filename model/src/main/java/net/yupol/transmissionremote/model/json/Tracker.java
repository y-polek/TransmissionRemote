package net.yupol.transmissionremote.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

public class Tracker implements Parcelable {

    @Json(name = "id") public int id;
    @Json(name = "tier") public int tier;
    @Json(name = "announce") public String announce;
    @Json(name = "scrape") public String scrape;

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
