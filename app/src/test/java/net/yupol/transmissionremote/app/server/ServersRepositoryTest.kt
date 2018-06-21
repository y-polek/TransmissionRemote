package net.yupol.transmissionremote.app.server

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LiveData
import net.yupol.transmissionremote.model.Server
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ServersRepositoryTest {

    companion object {
        val servers = arrayListOf(
                Server("1", "host1", 1),
                Server("2", "host2", 2),
                Server("3", "host3", 3),
                Server("4", "host4", 4),
                Server("5", "host5", 5)
        )

        val raspberryPi = Server("RaspberryPi", "192.168.1.2", 9091)
        val router = Server("Router", "192.168.1.1", 443)
        val laptop = Server("Laptop", "192.168.1.30", 9091)
    }

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repo: ServersRepository

    @Before
    fun setUp() {
        repo = ServersRepository(FakeServersStorage())
    }

    @Test
    fun testServerListIsEmptyAtStart() {
        val serversLiveData: LiveData<List<Server>> = repo.getServers()

        assertThat(serversLiveData.value).isEmpty()
    }

    @Test
    fun testAddServer() {
        repo.addServer(servers[0])

        assertThat(repo.getServers().value).containsOnly(servers[0])
    }

    @Test
    fun testRemoveServer() {
        repo.apply {
            addServer(servers[0])
            addServer(servers[1])
            addServer(servers[2])
        }

        repo.removeServer(servers[1])

        assertThat(repo.getServers().value).containsOnly(servers[0], servers[2])
    }

    @Test
    fun testServerListIsEmptyAfterRemovingAll() {
        repo.apply {
            addServer(servers[0])
            addServer(servers[1])
            addServer(servers[2])
        }

        repo.removeServer(servers[0])
        repo.removeServer(servers[1])
        repo.removeServer(servers[2])

        assertThat(repo.getServers().value).isEmpty()
    }

    @Test
    fun testActiveServerIsNullAtStart() {
        assertThat(repo.getActiveServer().value).isNull()
    }

    @Test
    fun testActiveServerIsFirstServerIfNotSet() {
        repo.apply {
            addServer(servers[0])
            addServer(servers[1])
            addServer(servers[2])
        }

        val activeServer = repo.getActiveServer().value

        assertThat(activeServer).isEqualTo(servers[0])
    }

    @Test
    fun testSetActiveServer() {
        val server = Server("test", "host", 1)

        repo.setActiveServer(server)

        assertThat(repo.getActiveServer().value).isEqualTo(server)
    }

    @Test
    fun testFirstServerBecomesActiveOnceActiveServerRemoved() {
        repo.apply {
            addServer(servers[0])
            addServer(servers[1])
            addServer(servers[2])
        }
        repo.setActiveServer(servers[1])

        repo.removeServer(servers[1])

        assertThat(repo.getActiveServer().value).isEqualTo(servers[0])
    }

    @Test
    fun testActiveServerBecomesNullOnAllServersRemoved() {
        repo.apply {
            addServer(servers[0])
            addServer(servers[1])
            addServer(servers[2])
        }
        repo.setActiveServer(servers[1])

        repo.removeServer(servers[2])
        repo.removeServer(servers[1])
        repo.removeServer(servers[0])

        assertThat(repo.getActiveServer().value).isNull()
    }

    @Test
    fun testRestoreServersFromStorage() {
        val repo = ServersRepository(FakeServersStorage(servers[0], servers[1]))

        assertThat(repo.getServers().value).containsOnly(servers[0], servers[1])
    }

    @Test
    fun testSaveServersToStorageOnceAddedToRepo() {
        val storage = FakeServersStorage()

        ServersRepository(storage).apply {
            addServer(servers[0])
            addServer(servers[1])
            addServer(servers[2])
        }

        assertThat(storage.readServers()).containsOnly(servers[0], servers[1], servers[2])
    }

    @Test
    fun testRemoveServersFromStorageOnceRemovedFromRepo() {
        val storage = FakeServersStorage(servers[0], servers[1], servers[2])

        ServersRepository(storage).apply {
            removeServer(servers[1])
        }

        assertThat(storage.readServers()).containsOnly(servers[0], servers[2])
    }

    @Test
    fun testSaveActiveServerToStorageOnceSetOnRepo() {
        val storage = FakeServersStorage()

        ServersRepository(storage).setActiveServer(servers[0])

        assertThat(storage.readActiveServer()).isEqualTo(servers[0])
    }

    @Test
    fun testGetServerById() {
        repo.apply {
            addServer(raspberryPi)
            addServer(router)
            addServer(laptop)
        }

        assertThat(repo.getServerById(raspberryPi.id)).isEqualTo(raspberryPi)
        assertThat(repo.getServerById(router.id)).isEqualTo(router)
        assertThat(repo.getServerById(laptop.id)).isEqualTo(laptop)
    }

    @Test
    fun testGetServerByIdReturnNullIfNotFound() {
        assertThat(repo.getServerById("fake_id")).isNull()
    }

    @Test
    fun testUpdateServer() {
        val server = Server("init name", "192.168.1.1", 9091)
        repo.addServer(server)

        val modifiedServer = Server.fromJson(server.toJson())
        modifiedServer.name = "new name"
        repo.updateServer(modifiedServer)

        val servers = repo.getServers().value
        assertThat(servers).doesNotContain(server)
        assertThat(servers).containsExactly(modifiedServer)
        assertThat(repo.getServerById(server.id)?.name).isEqualTo("new name")
    }

    @Test(expected = IllegalStateException::class)
    fun testUpdateServerThrowsExceptionIfNoServerFound() {
        repo.addServer(raspberryPi)

        repo.updateServer(laptop)
    }

    @Test
    fun testServerListPersistedAfterUpdate() {
        val storage = FakeServersStorage()
        val server = Server("init name", "192.168.1.1", 9091)
        val repo = ServersRepository(storage).apply {
            addServer(server)
        }

        server.name = "new name"
        repo.updateServer(server)

        assertThat(storage.readServers()[0].name).isEqualTo("new name")
    }

    @Test
    fun testUpdateServerUpdatesActiveServer() {
        val server = Server("init name", "192.168.1.1", 9091)
        repo.apply {
            addServer(raspberryPi)
            addServer(server)
            setActiveServer(server)
        }

        val modifiedServer = Server.fromJson(server.toJson())
        modifiedServer.name = "new name"
        repo.updateServer(modifiedServer)

        assertThat(repo.getActiveServer().value?.name).isEqualTo("new name")
    }

    @Test
    fun testPersistServers() {
        val storage = FakeServersStorage()
        val server = Server("init name", "192.168.1.1", 9091)
        val repo = ServersRepository(storage).apply {
            addServer(server)
        }

        server.name = "new name"
        assertThat(storage.readServers()[0].name).isEqualTo("init name")
        repo.persistServers()

        assertThat(storage.readServers()[0].name).isEqualTo("new name")
    }
}
