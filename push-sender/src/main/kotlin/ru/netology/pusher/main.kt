package ru.netology.pusher

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import java.io.FileInputStream
import java.io.IOException

object FcmSender {
    private const val CONFIG_FILE = "fcm.json"
    private const val DEVICE_TOKEN = token

    fun initialize() {
        try {
            FileInputStream(CONFIG_FILE).use { stream ->
                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(stream))
                    .build()

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options)
                }
            }
        } catch (e: IOException) {
            System.err.println("Failed to initialize FCM: ${e.message}")
            throw e
        }
    }

    fun sendLikeNotification(likeData: LikeData): String {
        val content = """
        {
            "userId": ${likeData.userId},
            "userName": "${likeData.userName}",
            "postId": ${likeData.postId},
            "postAuthor": "${likeData.postAuthor}"
        }
        """.trimIndent()

        return sendNotification("LIKE", content)
    }

    fun sendNewPostNotification(postData: PostData): String {
        val content = """
        {
            "postId": ${postData.postId},
            "authorId": ${postData.authorId},
            "authorName": "${postData.authorName}",
            "text": "${postData.text}",
            "previewText": "${postData.previewText}",
            "createdAt": "${postData.createdAt}"
        }
        """.trimIndent()

        return sendNotification("NEW_POST", content)
    }

    private fun sendNotification(action: String, content: String): String {
        try {
            val message = Message.builder()
                .putData("action", action)
                .putData("content", content)
                .setToken(DEVICE_TOKEN)
                .build()

            return FirebaseMessaging.getInstance().send(message)
        } catch (e: Exception) {
            System.err.println("Failed to send notification: ${e.message}")
            throw e
        }
    }
}

data class LikeData(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String
)

data class PostData(
    val postId: Long,
    val authorId: Long,
    val authorName: String,
    val text: String,
    val previewText: String,
    val createdAt: String
)

fun main() {
    try {
        // Инициализация FCM
        FcmSender.initialize()

        // Пример отправки уведомления о лайке
        val likeResponse = FcmSender.sendLikeNotification(
            LikeData(
                userId = 1,
                userName = "Vasiliy",
                postId = 2,
                postAuthor = "Netology"
            )
        )
        println("Like notification sent: $likeResponse")

        // Пример отправки уведомления о новом посте
        val postResponse = FcmSender.sendNewPostNotification(
            PostData(
                postId = 123,
                authorId = 456,
                authorName = "Иван Иванов",
                text = "Полный текст нового поста...",
                previewText = "Краткое содержание...",
                createdAt = "2023-05-01T12:00:00Z"
            )
        )
        println("New post notification sent: $postResponse")

    } catch (e: Exception) {
        System.err.println("Application error: ${e.message}")
        System.exit(1)
    }
}