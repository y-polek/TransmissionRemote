package net.yupol.transmissionremote.app.utils;

import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.TrackerState;
import net.yupol.transmissionremote.app.model.json.TrackerStats;

import static net.yupol.transmissionremote.app.utils.TextUtils.displayableDate;
import static net.yupol.transmissionremote.app.utils.TextUtils.displayableTime;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class DataBindingAdapters {

    public static class Tracker {

        @BindingAdapter("trackerTier")
        public static void setTier(TextView textView, TrackerStats trackerStats) {
            textView.setText(string(textView, R.string.trackers_tier_number, trackerStats.tier + 1));
        }

        @BindingAdapter("trackerHost")
        public static void setHost(TextView textView, TrackerStats trackerStats) {
            textView.setText(isNotEmpty(trackerStats.host)
                    ? trackerStats.host
                    : (isNotEmpty(trackerStats.announce) ? trackerStats.announce : string(textView, R.string.not_available)));
        }

        @BindingAdapter("trackerLastAnnounceStatus")
        public static void setLastAnnounceStatus(TextView textView, TrackerStats trackerStats) {

            if (trackerStats.hasAnnounced) {
                String lastAnnounceTime = displayableDate(trackerStats.lastAnnounceTime);
                if (trackerStats.lastAnnounceSucceeded) {

                    textView.setText(trackerStats.lastAnnouncePeerCount > 0
                            ? string(textView, R.string.trackers_last_announce, quantityString(textView,
                            R.plurals.trackers_last_announce_with_results,
                            trackerStats.lastAnnouncePeerCount,
                            lastAnnounceTime,
                            trackerStats.lastAnnouncePeerCount))
                            : string(textView,
                            R.string.trackers_last_announce,
                            lastAnnounceTime));
                } else {
                    StringBuilder resultTextBuilder = new StringBuilder();
                    if (isNotBlank(trackerStats.lastAnnounceResult)) {
                        resultTextBuilder.append(trackerStats.lastAnnounceResult).append(" - ");
                    }
                    resultTextBuilder.append(lastAnnounceTime);

                    textView.setText(string(textView, R.string.trackers_announce_error,
                            resultTextBuilder.toString()));
                }
            } else {
                textView.setText(string(textView, R.string.trackers_last_announce,
                        string(textView, R.string.not_available)));
            }
        }

        @BindingAdapter("trackerAnnounceState")
        public static void setAnnounceState(TextView textView, TrackerStats trackerStats) {

            switch (TrackerState.fromCode(trackerStats.announceState)) {
                case ACTIVE:
                    textView.setText(R.string.trackers_announce_in_progress);
                    break;
                case WAITING:
                    long timeUntilAnnounce = Math.max(trackerStats.nextAnnounceTime - System.currentTimeMillis() / 1000, 0);
                    textView.setText(string(textView,
                            R.string.trackers_next_announce_in,
                            displayableTime(timeUntilAnnounce)));
                    break;
                case QUEUED:
                    textView.setText(R.string.trackers_announce_is_queued);
                    break;
                case INACTIVE:
                    textView.setText(trackerStats.isBackup
                            ? R.string.trackers_tracked_will_be_used_as_backup
                            : R.string.trackers_announce_not_scheduled);
                    break;
                default:
                    textView.setText(string(textView,
                            R.string.trackers_unknown_announce_state,
                            trackerStats.announceState));
            }
        }

        @BindingAdapter("trackerScrapeStatus")
        public static void setScrapeStatus(TextView textView, TrackerStats trackerStats) {
            if (trackerStats.hasScraped) {
                String lastScrapeTime = displayableDate(trackerStats.lastScrapeTime);
                if (trackerStats.lastScrapeSucceeded) {
                    textView.setText(string(textView, R.string.trackers_last_scrape, lastScrapeTime));
                } else {
                    String errorText = isNotBlank(trackerStats.lastScrapeResult)
                            ? trackerStats.lastScrapeResult + " - " + lastScrapeTime
                            : lastScrapeTime;
                    textView.setText(string(textView, R.string.trackers_scrape_error, errorText));
                }
            } else {
                textView.setText(string(textView, R.string.trackers_last_scrape,
                        string(textView, R.string.not_available)));
            }
        }

        @BindingAdapter("trackerSeeders")
        public static void setSeeders(TextView textView, TrackerStats trackerStats) {
            textView.setText(string(textView, R.string.trackers_seeders,
                    trackerStats.seederCount >= 0
                            ? String.valueOf(trackerStats.seederCount)
                            : string(textView, R.string.not_available)));
        }

        @BindingAdapter("trackerLeechers")
        public static void setLeechersSeeders(TextView textView, TrackerStats trackerStats) {
            textView.setText(string(textView, R.string.trackers_leechers,
                    trackerStats.leecherCount >= 0
                            ? String.valueOf(trackerStats.leecherCount)
                            : string(textView, R.string.not_available)));
        }

        @BindingAdapter("trackerDownloaded")
        public static void setDownloaded(TextView textView, TrackerStats trackerStats) {
            textView.setText(string(textView, R.string.trackers_downloaded,
                    trackerStats.downloadCount >= 0
                            ? String.valueOf(trackerStats.downloadCount)
                            : string(textView, R.string.not_available)));
        }
    }

    private static String string(@NonNull View view, @StringRes int resId, Object... formatArgs) {
        return view.getResources().getString(resId, formatArgs);
    }

    private static String quantityString(@NonNull View view, @PluralsRes int resId, int quantity, Object... formatArgs) {
        return view.getResources().getQuantityString(resId, quantity, formatArgs);
    }
}
