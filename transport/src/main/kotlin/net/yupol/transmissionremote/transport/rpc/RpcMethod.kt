package net.yupol.transmissionremote.transport.rpc

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RpcMethod(val name: String)