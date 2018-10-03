package net.yupol.transmissionremote.data.api.rpc

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RpcFields(val fields: Array<String>)