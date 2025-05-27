package ru.netology.nmedia.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {


    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.data["action"]?.let {
            when (Action.valueOf(it)) {
                Action.LIKE -> handleLike(Gson().fromJson(message.data["content"], Like::class.java))
            }
        }
    }

    override fun onNewToken(token: String) {
        println(token)
    }



    private fun handleLike(like: Like) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.notification_user_liked, like.userName, like.postAuthor))
                .build()
            NotificationManagerCompat.from(this).notify(Random.nextInt(100_000),notification)

        }

    }



    companion object {
        private const val CHANNEL_ID = "notifications"
    }
}

enum class Action {
    LIKE
}

data class Like(
    /**
     *           "userId": 1,
     *           "userName": "Vasiliy",
     *           "postId": 2,
     *           "postAuthor": "Netology"
     */
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)