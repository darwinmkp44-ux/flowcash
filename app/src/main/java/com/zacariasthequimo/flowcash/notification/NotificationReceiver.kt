package com.zacariasthequimo.flowcash.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zacariasthequimo.flowcash.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(appContext)
                val transactions = database.transactionDao().getAllTransactionsSync()

                when (action) {
                    ACTION_DAILY_REMINDER -> {
                        NotificationHelper.showDailyReminder(appContext)
                    }
                    ACTION_WEEKLY_SUMMARY -> {
                        val now = System.currentTimeMillis()
                        val weekAgo = now - 7L * 24 * 3600 * 1000
                        val weekTx = transactions.filter { it.date >= weekAgo }
                        val inc = weekTx.filter { it.type == "RECEITA" }.sumOf { it.amount }
                        val exp = weekTx.filter { it.type == "DESPESA" }.sumOf { it.amount }
                        NotificationHelper.showWeeklySummary(appContext, inc, exp, inc - exp)
                    }
                    ACTION_MONTHLY_SUMMARY -> {
                        val now = System.currentTimeMillis()
                        val monthAgo = now - 30L * 24 * 3600 * 1000
                        val monthTx = transactions.filter { it.date >= monthAgo }
                        val inc = monthTx.filter { it.type == "RECEITA" }.sumOf { it.amount }
                        val exp = monthTx.filter { it.type == "DESPESA" }.sumOf { it.amount }
                        NotificationHelper.showMonthlySummary(appContext, inc, exp, inc - exp)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_DAILY_REMINDER = "com.zacariasthequimo.flowcash.DAILY_REMINDER"
        const val ACTION_WEEKLY_SUMMARY = "com.zacariasthequimo.flowcash.WEEKLY_SUMMARY"
        const val ACTION_MONTHLY_SUMMARY = "com.zacariasthequimo.flowcash.MONTHLY_SUMMARY"

        fun scheduleDailyReminder(context: Context) {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = ACTION_DAILY_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val calendar = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 20)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                if (before(java.util.Calendar.getInstance())) {
                    add(java.util.Calendar.DAY_OF_MONTH, 1)
                }
            }
            alarmManager.setInexactRepeating(
                android.app.AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                android.app.AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }

        fun scheduleWeeklySummary(context: Context) {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = ACTION_WEEKLY_SUMMARY
            }
            val pendingIntent = PendingIntent.getBroadcast(context, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val calendar = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
                set(java.util.Calendar.HOUR_OF_DAY, 9)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                if (before(java.util.Calendar.getInstance())) {
                    add(java.util.Calendar.WEEK_OF_YEAR, 1)
                }
            }
            alarmManager.setInexactRepeating(
                android.app.AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                7L * 24 * 3600 * 1000,
                pendingIntent
            )
        }

        fun scheduleMonthlySummary(context: Context) {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = ACTION_MONTHLY_SUMMARY
            }
            val pendingIntent = PendingIntent.getBroadcast(context, 2, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val calendar = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.DAY_OF_MONTH, 1)
                set(java.util.Calendar.HOUR_OF_DAY, 10)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                if (before(java.util.Calendar.getInstance())) {
                    add(java.util.Calendar.MONTH, 1)
                }
            }
            alarmManager.setInexactRepeating(
                android.app.AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                30L * 24 * 3600 * 1000,
                pendingIntent
            )
        }

        fun cancelAll(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            listOf(0, 1, 2).forEach { id ->
                val intent = Intent(context, NotificationReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(context, id, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                alarmManager.cancel(pendingIntent)
            }
        }
    }
}
