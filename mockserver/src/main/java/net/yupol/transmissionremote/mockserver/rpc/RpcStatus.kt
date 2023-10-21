package net.yupol.transmissionremote.mockserver.rpc

enum class RpcStatus(val status: Int) {
    STOPPED(0),
    CHECK_WAIT(1),
    CHECK(2),
    DOWNLOAD_WAIT(3),
    DOWNLOAD(4),
    SEED_WAIT(5),
    SEED(6)
}
