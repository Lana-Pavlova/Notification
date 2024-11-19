package com.example.mynotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri

class MainActivity : AppCompatActivity() {
    private lateinit var manager: NotificationManagerCompat

    companion object {
        val NORMAL_CHANNEL = "NORMAL_CHANNEL"
        val IMPORTANT_CHANNEL = "IMPORTANT_CHANNEL"
        const val SIMPLE_NOTIFICATION_ID = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        manager = NotificationManagerCompat.from(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name =
                resources.getString(R.string.NOT_IMPORTANT_CHANNEL_NAME)
            val channel = NotificationChannel(
                NORMAL_CHANNEL, name,
                NotificationManager.IMPORTANCE_LOW
            )
            val description =
                resources.getString(R.string.NOT_IMPORTANT_CHANNEL_DESCRIPTION)
            channel.description = description
            channel.enableVibration(false)
            manager.createNotificationChannel(channel)
        }
    }

    fun simpleNotification(view: View) {
        val builder = NotificationCompat.Builder(this, NORMAL_CHANNEL)
        builder
            .setSmallIcon(android.R.drawable.btn_star)
            .setContentTitle("Простое оповещение")
            .setContentText("Что-то важное произошло")
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    android.R.drawable.btn_star_big_on
                )
            )
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //    public void onRequestPermissionsResult(int requestCode, String[l permissions,
            //    int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
            return
        }
        manager.notify(R.id.SIMPLE_NOTIFICATION_ID, builder.build())

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение получено, теперь можно отправить уведомление
                simpleNotification(View(this))
            } else {
                // Разрешение не получено, обработайте это соответствующим образом
                // Например, покажите пользователю сообщение о том, что разрешение необходимо
            }
        }
    }

    fun simpleCancel(view: View) {
        manager.cancel(R.id.SIMPLE_NOTIFICATION_ID)
    }

    fun browserNotification(view: View) {
        val a2 = Intent(this, Activity2::class.java)
        val pa2 = PendingIntent.getActivity(
            this, R.id.BROWSER_PENDING_ID,
            a2, PendingIntent.FLAG_MUTABLE
        )
        val builder = NotificationCompat.Builder(this, NORMAL_CHANNEL)
        builder
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setContentTitle("Запустить браузер")
            .setContentText("Посмотреть google.com")
            .setContentIntent(pa2)
            .setAutoCancel(true)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        manager.notify(R.id.GOOGLE_NOTIFICATION_ID, builder.build())
    }

    fun complexNotification(view: View) {
        val browser = Intent(Intent.ACTION_VIEW)
        browser.setData(Uri.parse("https://gmir.ru/"))
        val browsePI = PendingIntent.getActivity(
            this,
            R.id.BROWSER_PENDING_ID, browser, PendingIntent.FLAG_IMMUTABLE
        )
        val map = Intent(Intent.ACTION_VIEW)
        map.setData(Uri.parse("geo: 59.931688, 30.301136"))
        val mapPI = PendingIntent.getActivity(
            this, R.id.MAP_PENDING_ID, map,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, NORMAL_CHANNEL)
        builder
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setContentTitle("Экскурсия")
            .setContentText("Экскурсия начнётся через 5 минут")
        builder.addAction(
            NotificationCompat.Action(
                android.R.drawable.btn_star,
                "В браузере",
                browsePI
            )
        )
        builder.addAction(
            NotificationCompat.Action(
                android.R.drawable.btn_star,
                "На карте",
                mapPI
            )
        )
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        manager.notify(R.id.HISTORY_NOTIFICATION_ID, builder.build())
    }
}