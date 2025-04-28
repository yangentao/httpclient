@file:Suppress("unused")

package io.github.yangentao.httpclient

import java.util.*
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

class HttpHeadersMap {
    val allMap = TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER)

    var connection: String? by HeaderDelegate
    var userAgent: String? by HeaderDelegate
    var accept: String? by HeaderDelegate
    var acceptCharset: String? by HeaderDelegate
    var acceptLanguage: String? by HeaderDelegate
    var authorization: String? by HeaderDelegate
    var contentType: String? by HeaderDelegate
    var range: String? by HeaderDelegate

    operator fun get(key: String): String? {
        return allMap[key]
    }

    operator fun set(key: String, value: String?) {
        if (value == null || value.isEmpty()) {
            allMap.remove(key)
        } else {
            allMap[key] = value
        }
    }

    fun remove(key: String) {
        allMap.remove(key)
    }

    fun add(key: String, value: String) {
        allMap[key] = value
    }

    fun add(vararg ps: Pair<String, String>) {
        for (p in ps) {
            allMap[p.first] = p.second
        }
    }

    //[from, to]
    fun range(from: Int, to: Int) {
        range = "bytes=$from-$to"
    }

    fun range(from: Int) {
        range = "bytes=$from-"
    }

    fun authBasic(user: String, pwd: String) {
        authorization = "Basic ${Base64.getUrlEncoder().encodeToString("$user:$pwd".toByteArray())}"
    }

    fun authBearer(token: String) {
        authorization = "Bearer $token"
    }

}

internal object HeaderDelegate {
    operator fun setValue(thisRef: HttpHeadersMap, property: KProperty<*>, value: String?) {
        if (value == null) {
            thisRef.allMap.remove(property.headerName)
        } else {
            thisRef.allMap[property.headerName] = value
        }
    }

    operator fun getValue(thisRef: HttpHeadersMap, property: KProperty<*>): String? {
        return thisRef.allMap[property.headerName]
    }
}

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class HeaderName(val value: String)

internal val KProperty<*>.headerName: String get() = this.findAnnotation<HeaderName>()?.value ?: this.name.headerKeyFormat