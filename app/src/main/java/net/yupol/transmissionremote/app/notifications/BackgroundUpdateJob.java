package net.yupol.transmissionremote.app.notifications;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.model.Server;
import net.yupol.transmissionremote.model.json.Torrent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import net.yupol.transmissionremote.transport.rpc.RpcArgs;
import net.yupol.transmissionremote.transport.Transport;

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
            new Transport(server).api().torrentList(RpcArgs.torrentGet())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<List<Torrent>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            requests.add(d);
                        }

                        @Override
                        public void onSuccess(List<Torrent> torrents) {
                            finishedTorrentsNotificationManager.checkForFinishedTorrents(server, torrents);
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
