package net.yupol.transmissionremote.transport.rpc

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RpcBooleanArg(val name: String, val value: Boolean)