package com.example.datn.utils.Extension

import android.os.Looper
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class UploadRequestBody(
    private val file: File, private val contentType: String
) : RequestBody() {

//    interface UploadCallBack {
//        fun uploadProgressbar(percentage: Int)
//    }

//    inner class ProgressUpdate(
//        private val upload: Long, private val total: Long
//    ) : Runnable {
//        override fun run() {
//            callback.uploadProgressbar(100 * (upload / total).toInt())
//        }
//    }


    override fun contentLength(): Long {
        return file.length()
    }


    //Xác định kiểu nội dung dữ liệu cần tải lên
    override fun contentType(): MediaType? {
        return "$contentType/*".toMediaTypeOrNull()
    }


    //THực hiện ghi dữ liệu đồng thời upload progressbar
    override fun writeTo(sink: BufferedSink) {
        val length = file.length()

        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(file)
        var upload = 0L
        fileInputStream.use { inputStream ->
            var read: Int
            val hanler = android.os.Handler(Looper.getMainLooper())
            while (inputStream.read(buffer).also { read = it } != -1) {
            //    hanler.post(ProgressUpdate(upload, length))
                upload += read
                sink.write(buffer, 0, read)
            }
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 1024
    }
}