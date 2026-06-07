package com.zacariasthequimo.flowcash.notification

import android.Manifest
import android.annotation.SuppressLint
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
import com.zacariasthequimo.flowcash.MainActivity

object NotificationHelper {
    const val CHANNEL_REMINDER = "flowcash_reminder"
    const val CHANNEL_SUMMARY = "flowcash_summary"
    const val CHANNEL_ALERT = "flowcash_alert"

    private const val NOTIFICATION_ID_DAILY = 1001
    private const val NOTIFICATION_ID_WEEKLY = 1002
    private const val NOTIFICATION_ID_MONTHLY = 1003
    private const val NOTIFICATION_ID_GOAL = 1004
    private const val NOTIFICATION_ID_TRANSACTION = 1005

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(NotificationChannel(
            CHANNEL_REMINDER, "Lembretes", NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Lembretes diários para registrar transações" })
        manager.createNotificationChannel(NotificationChannel(
            CHANNEL_SUMMARY, "Resumos", NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Resumos semanais e mensais" })
        manager.createNotificationChannel(NotificationChannel(
            CHANNEL_ALERT, "Alertas", NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Alertas de metas e novas transações" })
    }

    private fun canNotify(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    @SuppressLint("MissingPermission")
    fun showDailyReminder(context: Context) {
        if (!canNotify(context)) return
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_DAILY,
            NotificationCompat.Builder(context, CHANNEL_REMINDER)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Lembrete Diário")
                .setContentText("Não se esqueça de registrar suas transações de hoje!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent).setAutoCancel(true).build())
    }

    @SuppressLint("MissingPermission")
    fun showWeeklySummary(context: Context, income: Double, expenses: Double, net: Double) {
        if (!canNotify(context)) return
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val content = "Receitas: +${formatAmount(income)} | Despesas: -${formatAmount(expenses)} | Saldo: ${formatAmount(net)}"
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_WEEKLY,
            NotificationCompat.Builder(context, CHANNEL_SUMMARY)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Resumo Semanal").setContentText(content)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent).setAutoCancel(true).build())
    }

    @SuppressLint("MissingPermission")
    fun showMonthlySummary(context: Context, income: Double, expenses: Double, net: Double) {
        if (!canNotify(context)) return
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 2, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val content = "Receitas: +${formatAmount(income)} | Despesas: -${formatAmount(expenses)} | Saldo: ${formatAmount(net)}"
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_MONTHLY,
            NotificationCompat.Builder(context, CHANNEL_SUMMARY)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Resumo Mensal").setContentText(content)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent).setAutoCancel(true).build())
    }

    @SuppressLint("MissingPermission")
    fun showGoalAlert(context: Context, goalTitle: String, progress: Double, remaining: Double) {
        if (!canNotify(context)) return
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 3, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val body = "Faltam ${formatAmount(remaining)} para atingir a meta \"$goalTitle\" (${"%.0f".format(progress)}%)"
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_GOAL,
            NotificationCompat.Builder(context, CHANNEL_ALERT)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Meta Próxima!").setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent).setAutoCancel(true).build())
    }

    @SuppressLint("MissingPermission")
    fun showTransactionAlert(context: Context, title: String, category: String, amount: Double, isExpense: Boolean) {
        if (!canNotify(context)) return
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 4, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val prefix = if (isExpense) "Gastou" else "Recebeu"
        val typeLabel = if (isExpense) "Nova Despesa" else "Nova Receita"
        val body = "$prefix ${formatAmount(amount)} MZN em $category ($title)"
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_TRANSACTION,
            NotificationCompat.Builder(context, CHANNEL_ALERT)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(typeLabel).setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent).setAutoCancel(true).build())
    }

    private fun formatAmount(amount: Double): String {
        return if (amount >= 1000) String.format(java.util.Locale.US, "%.1fk", amount / 1000)
        else String.format(java.util.Locale.US, "%.0f", amount)
    }
}
