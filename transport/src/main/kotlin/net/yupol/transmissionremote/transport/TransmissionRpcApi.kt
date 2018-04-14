package net.yupol.transmissionremote.transport

import com.serjltt.moshi.adapters.FirstElement
import com.serjltt.moshi.adapters.Wrapped
import io.reactivex.Completable
import io.reactivex.Single
import net.yupol.transmissionremote.model.FreeSpace
import net.yupol.transmissionremote.model.TorrentMetadata.*
import net.yupol.transmissionremote.model.json.ServerSettings
import net.yupol.transmissionremote.model.json.Torrent
import net.yupol.transmissionremote.model.json.TorrentInfo
import net.yupol.transmissionremote.transport.rpc.*
import retrofit2.http.Body
import retrofit2.http.POST

interface TransmissionRpcApi {

    @POST("./")
    @RpcMethod("torrent-get")
    @RpcFields(fields = [
        ID, NAME, PERCENT_DONE, TOTAL_SIZE, ADDED_DATE, STATUS, RATE_DOWNLOAD, RATE_UPLOAD,
        UPLOADED_EVER, UPLOAD_RATIO, ETA, ERROR, ERROR_STRING, IS_FINISHED, SIZE_WHEN_DONE,
        LEFT_UNTIL_DONE, PEERS_GETTING_FROM_US, PEERS_SENDING_TO_US, WEBSEEDS_SENDING_TO_US,
        QUEUE_POSITION, RECHECK_PROGRESS, DONE_DATE])
    @Wrapped(path = ["arguments", "torrents"])
    fun torrentList(@Body @RpcArg("ids") vararg ids: Int): Single<List<Torrent>>

    @POST("./")
    @RpcMethod("torrent-get")
    @RpcFields(fields = [
        ID, FILES, FILE_STATS, BANDWIDTH_PRIORITY, HONORS_SESSION_LIMITS, DOWNLOAD_LIMITED,
        DOWNLOAD_LIMIT, UPLOAD_LIMITED, UPLOAD_LIMIT, SEED_RATIO_LIMIT, SEED_RATIO_MODE,
        SEED_IDLE_LIMIT, SEED_IDLE_MODE, HAVE_UNCHECKED, HAVE_VALID, SIZE_WHEN_DONE,
        LEFT_UNTIL_DONE, DESIRED_AVAILABLE, PIECE_COUNT, PIECE_SIZE, DOWNLOAD_DIR, IS_PRIVATE,
        CREATOR, DATE_CREATED, COMMENT, DOWNLOAD_EVER, CORRUPT_EVER, UPLOADED_EVER, ADDED_DATE,
        ACTIVITY_DATE, SECONDS_DOWNLOADING, SECONDS_SEEDING, PEERS, TRACKERS, TRACKER_STATS])
    @Wrapped(path = ["arguments", "torrents"])
    @FirstElement
    fun torrentInfo(@Body @RpcArg("ids") vararg ids: Int): Single<TorrentInfo>

    @POST("./")
    @RpcMethod("session-get")
    @Wrapped(path = ["arguments"])
    fun serverSettings(@Body args: Map<String, @JvmSuppressWildcards Any> = emptyMap()): Single<ServerSettings>

    @POST("./")
    @RpcMethod("session-set")
    fun setServerSettings(@Body args: Map<String, @JvmSuppressWildcards Any>): Completable

    @POST(".")
    @RpcMethod("torrent-start")
    fun startTorrents(@Body @RpcArg("ids") vararg ids: Int): Completable

    @POST(".")
    @RpcMethod("torrent-start-now")
    fun startTorrentsNoQueue(@Body @RpcArg("ids") vararg ids: Int): Completable

    @POST(".")
    @RpcMethod("torrent-stop")
    fun stopTorrents(@Body @RpcArg("ids") vararg ids: Int): Completable

    @POST(".")
    @RpcMethod("torrent-reannounce")
    fun reannounceTorrents(@Body @RpcArg("ids") vararg ids: Int): Completable

    @POST(".")
    @RpcMethod("torrent-verify")
    fun verifyTorrents(@Body @RpcArg("ids") vararg ids: Int): Completable

    @POST(".")
    @RpcMethod("torrent-rename-path")
    fun renameTorrent(@Body args: Map<String, @JvmSuppressWildcards Any>): Completable

    @POST(".")
    @RpcMethod("torrent-set-location")
    fun setTorrentLocation(@Body args: Map<String, @JvmSuppressWildcards Any>): Completable

    @POST(".")
    @RpcMethod("free-space")
    @Wrapped(path = ["arguments"])
    fun freeSpace(@Body @RpcArg("path") path: String): Single<FreeSpace>

    @POST(".")
    @RpcMethod("torrent-remove")
    @RpcBooleanArg(name = "delete-local-data", value = false)
    fun removeTorrents(@Body @RpcArg("ids") vararg ids: Int): Completable

    @POST(".")
    @RpcMethod("torrent-remove")
    @RpcBooleanArg(name = "delete-local-data", value = true)
    fun removeTorrentsAndDeleteData(@Body @RpcArg("ids") vararg ids: Int): Completable
}
