package net.yupol.transmissionremote.domain

data class Server(
        val name: String,
        val host: String,
        val login: String?,
        val password: String)
