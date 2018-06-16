package net.yupol.transmissionremote.app.server

import net.yupol.transmissionremote.app.InMemorySharedPreferences
import net.yupol.transmissionremote.model.Server
import org.assertj.core.api.Assertions.*
import org.junit.Test

class SharedPrefsServersStorageTest {

    companion object {

        val raspberryPi = Server("RaspberryPi", "192.168.1.2", 9091)
        val router = Server("Router", "192.168.1.1", 443)
        val laptop = Server("Laptop", "192.168.1.30", 9091)
    }

    private val storage = SharedPrefsServersStorage(InMemorySharedPreferences())

    @Test
    fun testWriteServers() {
        storage.writeServers(listOf(raspberryPi, router, laptop))

        val servers = storage.readServers()

        assertThat(servers).containsExactly(raspberryPi, router, laptop)
    }

    @Test
    fun testWriteEmptyServerList() {
        storage.writeServers(listOf())

        val servers = storage.readServers()

        assertThat(servers).isEmpty()
    }

    @Test
    fun testWriteActiveServer() {
        storage.writeActiveServer(router)

        val server = storage.readActiveServer()

        assertThat(server).isEqualTo(router)
    }

    @Test
    fun testWriteNullActiveServer() {
        storage.writeActiveServer(null)

        assertThat(storage.readActiveServer()).isNull()
    }

    @Test
    fun testWriteNullClearsActiveServer() {
        storage.writeActiveServer(router)
        storage.writeActiveServer(null)

        assertThat(storage.readActiveServer()).isNull()
    }
}