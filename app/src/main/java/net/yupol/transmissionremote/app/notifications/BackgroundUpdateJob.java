package net.yupol.transmissionremote.app.notifications;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.model.mapper.ServerMapper;
import net.yupol.transmissionremote.data.api.Transport;
import net.yupol.transmissionremote.data.api.model.TorrentEntity;
import net.yupol.transmissionremote.model.mapper.TorrentMapper;
import net.yupol.transmissionremote.model.Server;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BackgroundUpdateJob extends Job {

    private static final String TAG_UPDATE_TORRENTS = "tag_update_torrents";
    private static final String TAG = BackgroundUpdateJob.class.getSimpleName();

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Context context = getContext();

        List<Server> servers = TransmissionRemote.getApplication(context).getServers();
        final CountDownLatch countDownLatch = new CountDownLatch(servers.size());
        final FinishedTorrentsNotificationManager finishedTorrentsNotificationManager = new FinishedTorrentsNotificationManager(context);

        final CompositeDisposable requests = new CompositeDisposable();
        for (final Server server : servers) {
            new Transport(ServerMapper.toDomain(server)).api().torrentList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<List<TorrentEntity>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            requests.add(d);
                        }

                        @Override
                        public void onSuccess(List<TorrentEntity> torrents) {
                            finishedTorrentsNotificationManager.checkForFinishedTorrents(server, TorrentMapper.toViewModel(torrents));
                            countDownLatch.countDown();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "Failed to retrieve torrent list from " + server.getName() + "(" + server.getHost() + ")", e);
                            countDownLatch.countDown();
                        }
                    });
        }

        try {
            countDownLatch.await(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            requests.clear();
            return Result.FAILURE;
        }

        return Result.SUCCESS;
    }

    public static void schedule(boolean onlyUnmeteredNetwork) {
        Set<JobRequest> pendingJobs = JobManager.instance().getAllJobRequestsForTag(TAG_UPDATE_TORRENTS);
        if (pendingJobs.size() > 0) {
            return; // Already scheduled
        }

        JobRequest.Builder builder = new JobRequest.Builder(TAG_UPDATE_TORRENTS)
                .setPeriodic(JobRequest.MIN_INTERVAL, (long) (0.75 * JobRequest.MIN_INTERVAL));

        if (onlyUnmeteredNetwork) {
            builder.setRequiredNetworkType(JobRequest.NetworkType.UNMETERED);
        } else {
            builder.setRequiredNetworkType(JobRequest.NetworkType.CONNECTED);
        }

        builder.setRequirementsEnforced(true)
                .build()
                .schedule();
    }

    public static void cancelAll() {
        JobManager.instance().cancelAllForTag(TAG_UPDATE_TORRENTS);
    }

    public static class Creator implements JobCreator {

        @Nullable
        @Override
        public Job create(@NonNull String tag) {
            switch (tag) {
                case TAG_UPDATE_TORRENTS:
                    return new BackgroundUpdateJob();
                default:
                    return null;

            }
        }
    }
}
