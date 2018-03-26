package net.yupol.transmissionremote.transport

import net.yupol.transmissionremote.model.Server
import org.junit.Before
import org.junit.Test

class TransmissionRpcServiceTest {

    private lateinit var rpcApi: TransmissionRpcApi

    @Before
    fun setup() {
        val server = Server(name = "Ubuntu", host = "polek.ddns.net", port = 9093,
                login = "transmission", password = "transmission")

        rpcApi = Transport(server).api


    }

    @Test
    fun test() {

        rpcApi.torrentList()
                .subscribe(
                        { torrents ->
                            println("Torrents: $torrents")
                        },
                        { error ->
                            println("Error: ${error.message}")
                        }
                )

        rpcApi.serverSettings()
                .subscribe(
                        { settings ->
                            println("Settings: $settings")
                        },
                        { error ->
                            println("Error ${error.message}")
                        }
                )
    }
}