package moe.feng.danmaqua.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File

object FileUtils {

    suspend fun copyFileToUri(context: Context, file: File, uri: Uri) = withContext(IO) {
        val outputStream = context.contentResolver.openOutputStream(uri)!!
        file.inputStream().use {
            it.copyTo(outputStream)
        }
        outputStream.close()
    }

}