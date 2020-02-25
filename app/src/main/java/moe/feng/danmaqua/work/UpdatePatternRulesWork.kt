package moe.feng.danmaqua.work

import android.content.Context
import android.util.Log
import androidx.work.*
import kotlinx.coroutines.runBlocking
import moe.feng.danmaqua.Danmaqua
import moe.feng.danmaqua.Danmaqua.WORK_NAME_UPDATE_PATTERN_RULES
import moe.feng.danmaqua.api.DanmaquaApi
import moe.feng.danmaqua.data.DanmaquaDB
import moe.feng.danmaqua.model.PatternRulesItem
import moe.feng.danmaqua.util.ext.TAG
import java.util.concurrent.TimeUnit

class UpdatePatternRulesWork(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {

        fun enqueue(context: Context, interval: Long = Danmaqua.WORK_MIN_PERIODIC_INTERVAL) {
            val workManager = WorkManager.getInstance(context)
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresStorageNotLow(true)
                .setRequiresBatteryNotLow(true)
                .build()
            val workRequest = PeriodicWorkRequestBuilder<UpdatePatternRulesWork>(
                interval,
                TimeUnit.HOURS
            ).setConstraints(constraints).build()
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME_UPDATE_PATTERN_RULES,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun cancel(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelUniqueWork(WORK_NAME_UPDATE_PATTERN_RULES)
        }

    }

    override fun doWork(): Result {
        Log.i(TAG, "doWork() called.")
        return runBlocking {
            val dao = DanmaquaDB.instance.patternRules()
            val onlineRules: List<PatternRulesItem>
            try {
                onlineRules = DanmaquaApi.getPatternRules().data
            } catch (e: Exception) {
                return@runBlocking Result.failure()
            }
            dao.getAll().forEach { local ->
                onlineRules.find { online -> local.id == online.id }?.let {
                    Log.i(TAG, "doWork: updating ${local.id}")
                    local.title = it.title
                    local.desc = it.desc
                    local.pattern = it.pattern
                    local.committer = it.committer
                    dao.update(local)
                    if (local.selected) {
                        Danmaqua.Settings.notifyChanged(context)
                    }
                }
            }
            return@runBlocking Result.success()
        }
    }

}