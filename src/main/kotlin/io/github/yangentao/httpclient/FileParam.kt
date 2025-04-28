package io.github.yangentao.httpclient

import io.github.yangentao.httpbasic.HttpFileParam
import io.github.yangentao.httpbasic.Mimes
import java.io.File

//file, key, filename, mime都不能是空
/**
 * File Params, Multipart POST
 */
class FileParam(name: String, file: File, filename: String = file.name, mime: String = Mimes.ofFile(filename), val progress: HttpProgress? = null) : HttpFileParam(name, filename, file, mime)
