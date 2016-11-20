package net.yupol.transmissionremote.app.transport.request;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.common.primitives.Ints;

import net.yupol.transmissionremote.app.model.Priority;
import net.yupol.transmissionremote.app.model.json.TransferPriority;
import net.yupol.transmissionremote.app.model.limitmode.LimitMode;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
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

    public static class Builder implements RequestBuilder<TorrentSetRequest> {

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
        private int[] priorityHight;
        private int[] priorityNormal;
        private int[] priorityLow;

        private boolean changed = false;

        private Builder(int torrentId) {
            this.torrentId = torrentId;
        }

        public int getTorrentId() {
            return torrentId;
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

        public Builder filesWithPriority(Priority priority, int... fileIndices) {
            switch (priority) {
                case HIGH:
                    priorityHight = fileIndices;
                    break;
                case NORMAL:
                    priorityNormal = fileIndices;
                    break;
                case LOW:
                    priorityLow = fileIndices;
                    break;
            }
            return changedBuilder();
        }

        public Builder filesWithPriority(Priority priority, Collection<Integer> fileIndices) {
            return filesWithPriority(priority, Ints.toArray(fileIndices));
        }

        private Builder changedBuilder() {
            changed = true;
            return this;
        }

        public boolean isChanged() {
            return changed;
        }

        public Builder clear() {
            filesWantedIndices = null;
            filesUnwantedIndices = null;
            transferPriority = null;
            honorsSessionLimits = null;
            downloadLimited = null;
            downloadLimit = null;
            uploadLimited = null;
            uploadLimit = null;
            seedRatioMode = null;
            seedRatioLimit = null;
            seedIdleMode = null;
            seedIdleLimit = null;
            priorityHight = null;
            priorityNormal = null;
            priorityLow = null;

            changed = false;

            return this;
        }

        @Override
        public TorrentSetRequest build() {
            JSONObject args = new JSONObject();
            try {
                args.put("ids", new JSONArray(Collections.singleton(torrentId)));
                if (ArrayUtils.isNotEmpty(filesWantedIndices)) {
                    args.put("files-wanted", intArray(filesWantedIndices));
                }
                if (ArrayUtils.isNotEmpty(filesUnwantedIndices)) {
                    args.put("files-unwanted", intArray(filesUnwantedIndices));
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
                if (ArrayUtils.isNotEmpty(priorityHight)) {
                    args.put("priority-high", intArray(priorityHight));
                }
                if (ArrayUtils.isNotEmpty(priorityNormal)) {
                    args.put("priority-normal", intArray(priorityNormal));
                }
                if (ArrayUtils.isNotEmpty(priorityLow)) {
                    args.put("priority-low", intArray(priorityLow));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error while creating JSON object");
            }
            return new TorrentSetRequest(args);
        }

        private static JSONArray intArray(int[] array) {
            return new JSONArray(Arrays.asList(ArrayUtils.toObject(array)));
        }
    }
}
