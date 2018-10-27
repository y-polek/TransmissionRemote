package net.yupol.transmissionremote.data.api

class NoNetworkException(val inAirplaneMode: Boolean) : Exception("Network is not available")
