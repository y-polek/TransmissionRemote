package net.yupol.transmissionremote.data.api.rpc

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class RpcArg(val name: String)