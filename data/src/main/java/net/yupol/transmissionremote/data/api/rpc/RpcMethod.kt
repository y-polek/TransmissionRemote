package net.yupol.transmissionremote.data.api.rpc

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RpcMethod(val name: String)