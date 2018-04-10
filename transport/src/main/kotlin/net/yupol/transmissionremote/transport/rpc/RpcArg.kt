package net.yupol.transmissionremote.transport.rpc

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class RpcArg(val name: String)