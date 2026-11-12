package fr.estiam.traillog.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import fr.estiam.traillog.R
import fr.estiam.traillog.model.Trail
import fr.estiam.traillog.ui.TrailDetailActivity
import java.util.concurrent.TimeUnit

class TrailReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        ensureChannel(applicationContext)
        val name = inputData.getString("trail_name") ?: return Result.failure()
        val id = inputData.getString("trail_id") ?: return Result.failure()
        notify(applicationContext, id, name)
        return Result.success()
    }
    companion object {
        private const val CHANNEL = "traillog_hikes"
        fun ensureChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= 26) {
                val channel = NotificationChannel(CHANNEL, context.getString(R.string.notification_channel), NotificationManager.IMPORTANCE_DEFAULT)
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
            }
        }
        fun notifyNow(context: Context, trail: Trail) {
            ensureChannel(context); notify(context, trail.id, trail.name)
        }
        fun schedule(context: Context, trail: Trail) {
            val data = androidx.work.workDataOf("trail_id" to trail.id, "trail_name" to trail.name)
            WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<TrailReminderWorker>()
                .setInitialDelay(15, TimeUnit.MINUTES).setInputData(data).build())
        }
        private fun notify(context: Context, id: String, name: String) {
            if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
            val intent = Intent(context, TrailDetailActivity::class.java).putExtra(TrailDetailActivity.EXTRA_ID, id)
            val pending = PendingIntent.getActivity(context, id.hashCode(), intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            val notification = NotificationCompat.Builder(context, CHANNEL)
                .setSmallIcon(R.drawable.ic_mountain)
                .setContentTitle(context.getString(R.string.trail_reminder))
                .setContentText(name).setContentIntent(pending).setAutoCancel(true).build()
            NotificationManagerCompat.from(context).notify(id.hashCode(), notification)
        }
    }
}
