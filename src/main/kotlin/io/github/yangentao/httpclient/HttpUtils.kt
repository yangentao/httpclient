package io.github.yangentao.httpclient

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URLEncoder
import java.util.*

internal  val String.encodedURL: String
    get() {
        return URLEncoder.encode(this, Charsets.UTF_8)
    }

internal val UUID.hexText: String get() = String.format("%x%x", this.mostSignificantBits, this.leastSignificantBits)

@Throws(IOException::class)
internal fun copyStream(
    input: InputStream,
    closeIs: Boolean,
    os: OutputStream,
    closeOs: Boolean,
    total: Int,
    progress: HttpProgress?,
    delay: Long = 50
) {
    try {
        progress?.onStart(total)

        val buf = ByteArray(4096)
        var pre = System.currentTimeMillis()
        var recv = 0

        var n = input.read(buf)
        while (n != -1) {
            os.write(buf, 0, n)
            recv += n
            if (progress != null) {
                val curr = System.currentTimeMillis()
                if (curr - pre > delay) {
                    pre = curr
                    progress.onProgress(recv, total, if (total > 0) recv * 100 / total else 0)
                }
            }
            n = input.read(buf)
        }
        os.flush()
        progress?.onProgress(recv, total, if (total > 0) recv * 100 / total else 0)
        progress?.onFinish(true)
    } catch (ex: Exception) {
        printX(ex)
        progress?.onFinish(false)
    } finally {
        if (closeIs) {
            input.closeSafe()
        }
        if (closeOs) {
            os.closeSafe()
        }

    }
}

internal fun AutoCloseable.closeSafe() {
    try {
        this.close()
    } catch (_: Throwable) {
    }
}

internal fun printX(vararg vs: Any?) {
    val s = vs.joinToString(" ") {
        it?.toString() ?: "null"
    }
    println(s)
}

//accept => Accept
//userAgent => User-Agent
internal val String.headerKeyFormat: String
    get() {
        val sb = StringBuilder()
        for (ch: Char in this) {
            if (sb.isEmpty()) {
                sb.append(ch.uppercaseChar())
            } else if (ch.isUpperCase()) {
                sb.append('-').append(ch)
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

internal class SizeStream : OutputStream() {
    var size = 0
        private set

    @Throws(IOException::class)
    override fun write(oneByte: Int) {
        ++size
    }

    fun incSize(size: Int) {
        this.size += size
    }

}