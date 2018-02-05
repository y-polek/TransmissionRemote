package net.yupol.transmissionremote.app.notifications;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.octo.android.robospice.networkstate.DefaultNetworkStateChecker;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.DefaultRequestRunner;
import com.octo.android.robospice.request.RequestProcessorListener;
import com.octo.android.robospice.request.RequestProgressManager;
import com.octo.android.robospice.request.RequestRunner;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.notifier.DefaultRequestListenerNotifier;
import com.octo.android.robospice.request.notifier.SpiceServiceListenerNotifier;
import com.octo.android.robospice.retry.DefaultRetryPolicy;

import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.model.json.Torrents;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.SpiceTransportManager;
import net.yupol.transmissionremote.app.transport.request.Request;
import net.yupol.transmissionremote.app.transport.request.TorrentGetRequest;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BackgroundUpdateJob extends Job {

    private static final String TAG_UPDATE_TORRENTS = "tag_update_torrents";
    private static final String TAG = BackgroundUpdateJob.class.getSimpleName();

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Context context = getContext();

        List<Server> servers = TransmissionRemote.getApplication(context).getServers();
        final CountDownLatch countDownLatch = new CountDownLatch(servers.size());

        final SpiceTransportManager transportManager = new SpiceTransportManager();
        //transportManager.start(context);

        final FinishedTorrentsDetector finishedTorrentsDetector = new FinishedTorrentsDetector(context);

        for (final Server server : servers) {
            executeRequest(new TorrentGetRequest(), server, countDownLatch);
            /*transportManager.doRequest(new TorrentGetRequest(), server, new RequestListener<Torrents>() {
                @Override
                public void onRequestSuccess(Torrents torrents) {
                    finishedTorrentsDetector.checkForFinishedTorrents(server, torrents);
                    countDownLatch.countDown();
                }

                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Log.e(TAG, "Failed to retrieve torrent list from " + server.getName() + "(" + server.getHost() + ")");
                    countDownLatch.countDown();
                }
            });*/
        }

        try {
            countDownLatch.await(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            //transportManager.cancelAllRequests();
            // TODO: cancel requests
            return Result.FAILURE;
        }

        return Result.SUCCESS;
    }

    private <T> void executeRequest(Request<T> request, Server server, final CountDownLatch latch) {
        RequestRunner requestRunner = new DefaultRequestRunner(getContext(),
                new CacheManager() {
                    @Override public <T> T saveDataToCacheAndReturnData(T data, Object cacheKey) throws CacheSavingException, CacheCreationException {
                        return data;
                    }
                },
                Executors.newSingleThreadExecutor(),
                new RequestProgressManager(new RequestProcessorListener() {
                    @Override
                    public void requestsInProgress() {
                        Log.d(TAG, "requestInProgress");
                    }

                    @Override
                    public void allRequestComplete() {
                        Log.d(TAG, "allRequestComplete: ");
                        latch.countDown();
                    }
                }, new DefaultRequestListenerNotifier(), new SpiceServiceListenerNotifier()),
                new DefaultNetworkStateChecker()
        );

        request.setServer(server);
        request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));

        CachedSpiceRequest<T> cachedSpiceRequest = new CachedSpiceRequest<>(request, null, DurationInMillis.ALWAYS_EXPIRED);
        requestRunner.executeRequest(cachedSpiceRequest);
    }

    public static void schedule() {
        Set<JobRequest> pendingJobs = JobManager.instance().getAllJobRequestsForTag(TAG_UPDATE_TORRENTS);
        if (pendingJobs.size() > 0) {
            return; // Already scheduled
        }

        new JobRequest.Builder(TAG_UPDATE_TORRENTS)
                //.setPeriodic(JobRequest.MIN_INTERVAL, (long) (0.75 * JobRequest.MIN_INTERVAL))
                //.setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                //.setRequirementsEnforced(true)
                .startNow()
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
