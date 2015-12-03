package net.yupol.transmissionremote.app.transport.request;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import net.yupol.transmissionremote.app.model.json.LimitMode;
import net.yupol.transmissionremote.app.model.json.TransferPriority;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;

public class TorrentSetRequest extends Request<Void> implements Parcelable {

    private static final String TAG = TorrentSetRequest.class.getSimpleName();

    private JSONObject arguments;

    private TorrentSetRequest(JSONObject arguments) {
        super(Void.class);
        this.arguments = arguments;
    }

    @Override
    protected String getMethod() {
        return "torrent-set";
    }

    @Override
    protected JSONObject getArguments() {
        return arguments;
    }

    public static Builder builder(int torrentId) {
        return new Builder(torrentId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(arguments.toString());
    }

    public static final Creator<TorrentSetRequest> CREATOR = new Creator<TorrentSetRequest>() {
        @Override
        public TorrentSetRequest createFromParcel(Parcel in) {
            String argsStr = in.readString();
            try {
                return new TorrentSetRequest(new JSONObject(argsStr));
            } catch (JSONException e) {
                Log.e(TAG, "Failed to restore from parcel. Args string: " + argsStr, e);
                return new TorrentSetRequest(new JSONObject());
            }
        }

        @Override
        public TorrentSetRequest[] newArray(int size) {
            return new TorrentSetRequest[size];
        }
    };

    public static class Builder {

        private int torrentId;
        private int[] filesWantedIndices;
        private int[] filesUnwantedIndices;
        private TransferPriority transferPriority;
        private Boolean honorsSessionLimits;
        private Boolean downloadLimited;
        private Long downloadLimit;
        private Boolean uploadLimited;
        private Long uploadLimit;
        private LimitMode seedRatioMode;
        private Double seedRatioLimit;
        private LimitMode seedIdleMode;
        private Double seedIdleLimit;

        private boolean changed = false;

        private Builder(int torrentId) {
            this.torrentId = torrentId;
        }

        public Builder filesWanted(int... fileIndices) {
            filesWantedIndices = fileIndices;
            return changedBuilder();
        }

        public Builder filesUnwanted(int... fileIndices) {
            filesUnwantedIndices = fileIndices;
            return changedBuilder();
        }

        public Builder transferPriority(TransferPriority priority) {
            transferPriority = priority;
            return changedBuilder();
        }

        public Builder honorsSessionLimits(boolean honorsSessionLimits) {
            this.honorsSessionLimits = honorsSessionLimits;
            return changedBuilder();
        }

        public Builder downloadLimited(boolean isLimited) {
            this.downloadLimited = isLimited;
            return changedBuilder();
        }

        public Builder downloadLimit(long limit) {
            downloadLimit = limit;
            return changedBuilder();
        }

        public Builder uploadLimited(boolean isLimited) {
            this.uploadLimited = isLimited;
            return changedBuilder();
        }

        public Builder uploadLimit(long limit) {
            uploadLimit = limit;
            return changedBuilder();
        }

        public Builder seedRatioMode(LimitMode mode) {
            seedRatioMode = mode;
            return changedBuilder();
        }

        public Builder seedRatioLimit(double limit) {
            seedRatioLimit = limit;
            return changedBuilder();
        }

        public Builder seedIdleMode(LimitMode mode) {
            seedIdleMode = mode;
            return changedBuilder();
        }

        public Builder seedIdleLimit(double limit) {
            seedIdleLimit = limit;
            return changedBuilder();
        }

        private Builder changedBuilder() {
            changed = true;
            return this;
        }

        public boolean isChanged() {
            return changed;
        }

        public TorrentSetRequest build() {
            JSONObject args = new JSONObject();
            try {
                args.put("ids", new JSONArray(Collections.singleton(torrentId)));
                if (filesWantedIndices != null && filesWantedIndices.length > 0) {
                    args.put("files-wanted", new JSONArray(Arrays.asList(ArrayUtils.toObject(filesWantedIndices))));
                }
                if (filesUnwantedIndices != null && filesUnwantedIndices.length > 0) {
                    args.put("files-unwanted", new JSONArray(Arrays.asList(ArrayUtils.toObject(filesUnwantedIndices))));
                }
                if (transferPriority != null) {
                    args.put("bandwidthPriority", transferPriority.getModelValue());
                }
                if (honorsSessionLimits != null) {
                    args.put("honorsSessionLimits", honorsSessionLimits);
                }
                if (downloadLimited != null) {
                    args.put("downloadLimited", downloadLimited);
                }
                if (downloadLimit != null) {
                    args.put("downloadLimit", downloadLimit);
                }
                if (uploadLimited != null) {
                    args.put("uploadLimited", uploadLimited);
                }
                if (uploadLimit != null) {
                    args.put("uploadLimit", uploadLimit);
                }
                if (seedRatioMode != null) {
                    args.put("seedRatioMode", seedRatioMode.getValue());
                }
                if (seedRatioLimit != null) {
                    args.put("seedRatioLimit", seedRatioLimit);
                }
                if (seedIdleMode != null) {
                    args.put("seedIdleMode", seedIdleMode.getValue());
                }
                if (seedIdleLimit != null) {
                    args.put("seedIdleLimit", seedIdleLimit);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error while creating JSON object");
            }
            return new TorrentSetRequest(args);
        }
    }
}
