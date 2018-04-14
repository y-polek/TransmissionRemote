package net.yupol.transmissionremote.transport.rpc

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RpcFields(val fields: Array<String>)