package moe.feng.danmaqua.data

import android.content.Context
import com.google.code.regexp.Pattern
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.model.HistoryFile
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object HistoryManager {

    private const val TABLE_HEADERS: String = "timestamp,uid,username,text"
    private const val MAX_BUFFERED_LINES: Int = 5

    const val FILENAME_FORMAT = "%s-%04d%02d%02d.csv"
    const val FILENAME_REGEXP = "^(?<room>\\d+)-(?<y>\\d{4})(?<m>\\d{2})(?<d>\\d{2})\\.csv\$"

    private val pattern = Pattern.compile(FILENAME_REGEXP)

    private var currentHistoryFile: HistoryFile? = null
    private var bufferedWriter: BufferedWriter? = null

    private val writeLock: Lock = ReentrantLock()
    private val bufferedLines: AtomicInteger = AtomicInteger()

    suspend fun startRecord(context: Context, roomId: Long) = withContext(Dispatchers.IO) {
        if (currentHistoryFile?.roomId == roomId) {
            return@withContext
        }
        ensureSavePath(context)
        writeLock.withLock {
            try {
                bufferedWriter?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            bufferedWriter = null

            currentHistoryFile = historyFileFrom(context, roomId, Calendar.getInstance()).also {
                if (it.file.isDirectory) {
                    it.file.deleteRecursively()
                }
                if (!it.file.exists()) {
                    it.file.createNewFile()
                    it.file.bufferedWriter().use { writer ->
                        writer.appendln(TABLE_HEADERS)
                    }
                }
                bufferedLines.set(0)
                bufferedWriter = FileOutputStream(it.file, true).bufferedWriter()
            }
        }
    }

    suspend fun stopRecord() = withContext(Dispatchers.IO) {
        if (bufferedWriter != null) {
            writeLock.withLock {
                try {
                    bufferedWriter?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                bufferedWriter = null
            }
        }
        currentHistoryFile = null
    }

    suspend fun record(item: BiliChatDanmaku) = withContext(Dispatchers.IO) {
        if (!isRecording()) {
            return@withContext
        }
        writeLock.withLock {
            bufferedWriter?.appendln(danmakuToLine(item))
            if (bufferedLines.incrementAndGet() == MAX_BUFFERED_LINES) {
                bufferedLines.set(0)
                bufferedWriter?.flush()
            }
        }
    }

    suspend fun isRecording(): Boolean = withContext(Dispatchers.IO) {
        writeLock.withLock { currentHistoryFile != null && bufferedWriter != null }
    }

    suspend fun isRecording(roomId: Long): Boolean = withContext(Dispatchers.IO) {
        writeLock.withLock { currentHistoryFile?.roomId == roomId }
    }

    suspend fun listHistoryFiles(
        context: Context
    ): List<HistoryFile> = withContext(Dispatchers.IO) {
        val savePath = getSavePath(context)
        savePath.listFiles()?.mapNotNull { toHistoryFile(it) } ?: emptyList()
    }

    suspend fun findHistoryFile(
        context: Context, roomId: Long, date: Calendar
    ): HistoryFile? = withContext(Dispatchers.IO) {
        historyFileFrom(context, roomId, date).takeIf { it.file.isFile }
    }

    private fun danmakuToLine(item: BiliChatDanmaku): String {
        return with(item) { "$timestamp,$senderUid,$senderName,$text" }
    }

    private fun getSavePath(context: Context): File {
        return File(context.filesDir, "history")
    }

    private suspend fun ensureSavePath(context: Context) = withContext(Dispatchers.IO) {
        val savePath = getSavePath(context)
        if (savePath.isFile) {
            savePath.delete()
        }
        if (!savePath.exists()) {
            savePath.mkdirs()
        }
    }

    private suspend fun toHistoryFile(file: File): HistoryFile? = withContext(Dispatchers.IO) {
        val matcher = pattern.matcher(file.name)
        if (matcher.matches()) {
            try {
                return@withContext HistoryFile(
                    matcher.group("room").toLong(),
                    matcher.group("y").toInt(),
                    matcher.group("m").toInt(),
                    matcher.group("d").toInt(),
                    file
                )
            } catch (ignored: Exception) {

            }
        }
        return@withContext null
    }

    private fun historyFileFrom(context: Context, roomId: Long, date: Calendar): HistoryFile {
        val y = date.get(Calendar.YEAR)
        val m = date.get(Calendar.MONTH) + 1
        val d = date.get(Calendar.DAY_OF_MONTH)
        val file = File(getSavePath(context), FILENAME_FORMAT.format(roomId, y, m, d))
        return HistoryFile(roomId, y, m, d, file)
    }

}