package com.naranja.webview

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.util.Date

class JavaScriptInterface(private val context: Context) {
    @JavascriptInterface
    @Throws(IOException::class)
    fun getBase64FromBlobData(base64Data: String) {
        convertBase64StringToPdfAndStoreIt(base64Data)
    }

    @Throws(IOException::class)
    private fun convertBase64StringToPdfAndStoreIt(base64PDf: String) {
        Log.e("BASE 64", base64PDf)
        val notificationId = 1
        val currentDateTime = DateFormat.getDateTimeInstance().format(Date())
        val dwldsPath = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).toString() + "/YourFileName_" + currentDateTime + "_.pdf"
        )
        val pdfAsBytes =
            Base64.decode(base64PDf.replaceFirst("^data:application/pdf;base64,".toRegex(), ""), 0)
        FileOutputStream(dwldsPath, false).use { os ->
            os.write(pdfAsBytes)
            os.flush()
        }
        if (dwldsPath.exists()) {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            val apkURI = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                dwldsPath
            )
            intent.setDataAndType(
                apkURI,
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val pendingIntent = PendingIntent.getActivity(
                context,
                1,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, "name", NotificationManager.IMPORTANCE_LOW)
            val notification = Notification.Builder(context, CHANNEL_ID)
                .setContentText("You have got something new!")
                .setContentTitle("File downloaded")
                .setContentIntent(pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(R.drawable.sym_action_chat)
                .build()
            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.notify(notificationId, notification)
        }
        Toast.makeText(context, "PDF FILE DOWNLOADED!", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val CHANNEL_ID = "MYCHANNEL"
        fun getBase64StringFromBlobUrl(blobUrl: String): String {
            return if (blobUrl.startsWith("blob")) {
                "javascript: var xhr = new XMLHttpRequest();" +
                        "xhr.open('GET', '" + blobUrl + "', true);" +
                        "xhr.setRequestHeader('Content-type','application/pdf');" +
                        "xhr.responseType = 'blob';" +
                        "xhr.onload = function(e) {" +
                        "    if (this.status == 200) {" +
                        "        var blobPdf = this.response;" +
                        "        var reader = new FileReader();" +
                        "        reader.readAsDataURL(blobPdf);" +
                        "        reader.onloadend = function() {" +
                        "            base64data = reader.result;" +
                        "            Android.getBase64FromBlobData(base64data);" +
                        "        }" +
                        "    }" +
                        "};" +
                        "xhr.send();"
            } else "javascript: console.log('It is not a Blob URL');"
        }
    }
}