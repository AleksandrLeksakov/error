package ru.netology.nmedia.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R


class FCMService : FirebaseMessagingService() {
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        try {
            val action = message.data["action"] ?: run {
                println("Received message without action")
                return
            }

            val content = message.data["content"] ?: run {
                println("Received message without content")
                return
            }

            when (action.safeToAction()) {
                Action.LIKE -> handleLike(gson.fromJson(content, Like::class.java))
                Action.NEW_POST -> handleNewPost(gson.fromJson(content, Post::class.java))
                Action.UNKNOWN -> println("Unknown action received: $action")
            }
        } catch (e: Exception) {
            println("Error processing FCM message: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onNewToken(token: String) {
        println("New FCM token: $token")
        // Здесь можно отправить токен на ваш сервер
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun handleLike(like: Like) {
        if (!hasNotificationPermission()) return

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.notification_user_liked, like.userName, like.postAuthor))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        showNotification(notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun handleNewPost(post: Post) {
        if (!hasNotificationPermission()) return

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("${post.authorName} опубликовал новый пост")
            .setContentText(post.previewText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(post.text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        showNotification(notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(notification: Notification) {
        try {
            // Явно указываем контекст сервиса
            val notificationManager = NotificationManagerCompat.from(this@FCMService)

            // Генерируем уникальный ID для уведомления
            val notificationId = generateNotificationId()

            // Показываем уведомление с обработкой возможных ошибок
            notificationManager.notify(notificationId, notification)
        } catch (e: Exception) {
            // Логируем ошибки более детально
            println("Failed to show notification: ${e.javaClass.simpleName} - ${e.message}")
            e.printStackTrace()
        }
    }

    private fun generateNotificationId(): Int {
        // Более надежный способ генерации ID
        return (System.currentTimeMillis() and 0xfffffff).toInt()
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Для версий ниже Android 13 разрешение не требуется
        }
    }

    companion object {
        private const val CHANNEL_ID = "notifications"
    }
}

enum class Action {
    LIKE,
    NEW_POST,
    UNKNOWN;

    companion object {
        fun safeValueOf(value: String): Action {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                UNKNOWN
            }
        }
    }
}

fun String.safeToAction(): Action = Action.safeValueOf(this)

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)

data class Post(
    val postId: Long,
    val authorId: Long,
    val authorName: String,
    val text: String,
    val previewText: String,
    val createdAt: String
)