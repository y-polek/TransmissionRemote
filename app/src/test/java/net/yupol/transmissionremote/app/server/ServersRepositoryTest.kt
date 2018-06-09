package net.yupol.transmissionremote.app.server

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LiveData
import net.yupol.transmissionremote.model.Server
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ServersRepositoryTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var repo: ServersRepository

    @Before
    fun setUp() {
        repo = ServersRepository()
    }

    @Test
    fun testServerListIsEmptyAtStart() {
        val serversLiveData: LiveData<List<Server>> = repo.getServers()

        assertThat(serversLiveData.value).isEmpty()
    }

    @Test
    fun testAddServer() {
        val server = Server("test", "192.168.1.1", 9091)

        repo.addServer(server)

        assertThat(repo.getServers().value).containsOnly(server)
    }

    @Test
    fun testRemoveServer() {
        val server1 = Server("1", "host1", 1)
        val server2 = Server("2", "host2", 2)
        val server3 = Server("3", "host3", 3)
        repo.apply {
            addServer(server1)
            addServer(server2)
            addServer(server3)
        }

        repo.removeServer(server2)

        assertThat(repo.getServers().value).containsOnly(server1, server3)
    }

    @Test
    fun testServerListIsEmptyAfterRemovingAll() {
        val server1 = Server("1", "host1", 1)
        val server2 = Server("2", "host2", 2)
        val server3 = Server("3", "host3", 3)
        repo.apply {
            addServer(server1)
            addServer(server2)
            addServer(server3)
        }

        repo.removeServer(server1)
        repo.removeServer(server2)
        repo.removeServer(server3)

        assertThat(repo.getServers().value).isEmpty()
    }

    @Test
    fun testActiveServerIsNullAtStart() {
        assertThat(repo.getActiveServer().value).isNull()
    }

    @Test
    fun testSetActiveServer() {
        val server = Server("test", "host", 1)

        repo.setActiveServer(server)

        assertThat(repo.getActiveServer().value).isEqualTo(server)
    }
}
