package com.sudoajay.historyprivacycleaner.firebase

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.RemoteMessage
import com.sudoajay.historyprivacycleaner.R
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class FirebaseNotification(var context: Context) {

    private var notificationManager: NotificationManager? = null
    private var notification: Notification? = null

    fun notifyCompat(
        remoteMessage: RemoteMessage.Notification?,
        builder: NotificationCompat.Builder
    ) { // local variable

        val title = remoteMessage!!.title
        val notificationText = remoteMessage.body
        val imageUrl = remoteMessage.imageUrl.toString()

        // now check for null notification manger
        if (notificationManager == null) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        // Default ringtone
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        builder
            // Set appropriate defaults for the notification light, sound,
            // and vibration.
            .setDefaults(Notification.DEFAULT_ALL) // Set required fields, including the small icon, the
            .setContentTitle(title)
            .setContentText(notificationText)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(uri) // Provide a large icon, shown with the notification in the

            .color = ContextCompat.getColor(context, R.color.colorPrimary)
        // If this notification relates to a past or upcoming event, you
        if (imageUrl != "null") {
            Log.e("MainActivityClass", imageUrl)

            val bpStyle =
                NotificationCompat.BigPictureStyle()
            bpStyle.bigPicture(getBitmapFromURL(imageUrl)).build()
            builder.setStyle(bpStyle)
        }

        // check if there ia data with empty
// more and view button classification
        notification = builder.build()

        notification!!.flags =
            notification!!.flags or (Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT)

        notifyNotification(notification!!)
    }

    private fun notifyNotification(notification: Notification) {
        notificationManager!!.notify(0, notification)
    }

    private fun getBitmapFromURL(strURL: String?): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            null
        }
    }

}



