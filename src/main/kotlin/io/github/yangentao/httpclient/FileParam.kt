package io.github.yangentao.httpclient

import java.io.File

//file, key, filename, mime都不能是空
/**
 * File Params, Multipart POST
 */
class FileParam(val key: String, val file: File, var filename: String = file.name, var mime: String = "application/octet-stream") {
    var progress: HttpProgress? = null

    fun mime(mime: String?): FileParam {
        if (mime != null) {
            this.mime = mime
        }
        return this
    }

    fun fileName(filename: String?): FileParam {
        if (filename != null) {
            this.filename = filename
        }
        return this
    }

    fun progress(progress: HttpProgress?): FileParam {
        this.progress = progress
        return this
    }

    override fun toString(): String {
        return "key=$key, filename=$filename, mime=$mime, file=$file"
    }
}