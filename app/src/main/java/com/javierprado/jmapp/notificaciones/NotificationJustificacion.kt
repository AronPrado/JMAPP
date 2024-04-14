package com.javierprado.jmapp.notificaciones

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.data.util.SharedPrefs

private const val CHANNEL_ID = "my_channel"
class NotificationJustificacion: BroadcastReceiver() {
    val firestore = FirebaseFirestore.getInstance()
    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationManager : NotificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val remoteInput = RemoteInput.getResultsFromIntent(intent)

        if (remoteInput!=null){
            val repliedText = remoteInput.getString("KEY_REPLY_TEXT")

            val mysharedPrefs = SharedPrefs(context)
            val friendid = mysharedPrefs.getValue("friendid")
            val chatroomid = mysharedPrefs.getValue("chatroomid")
            val friendname = mysharedPrefs.getValue("friendname")
//            val friendimage = mysharedPrefs.getValue("friendimage")


            val hashMap = hashMapOf<String, Any>("sender" to AnotherUtil.getUidLoggedIn(),
                "time" to AnotherUtil.getTime(),
                "receiver" to friendid!!,
                "message" to repliedText!!)

            firestore.collection("Messages").document(chatroomid!!)
                .collection("chats").document(AnotherUtil.getTime()).set(hashMap).addOnCompleteListener {

                    if (it.isSuccessful){
                    }
                }
            // this can be further improved if user in other chatroom and message comes, he can use notification
            // reply to send that exact room
            val setHashap = hashMapOf<String, Any>(
                "friendid" to friendid,
                "time" to AnotherUtil.getTime(),
                "sender" to AnotherUtil.getUidLoggedIn(),
                "message" to repliedText,
//                "friendsimage" to friendimage!!,
                "name" to friendname!!,
                "person" to "you",
            )

            firestore.collection("Conversation${AnotherUtil.getUidLoggedIn()}").document(friendid)
                .set(setHashap)

            val updateHashMap =
                hashMapOf<String, Any>("message" to repliedText, "time" to AnotherUtil.getTime(), "person" to friendname!!)

            firestore.collection("Conversation${friendid}").document(AnotherUtil.getUidLoggedIn())
                .update(updateHashMap)

            val sharedCustomPref = SharedPrefs(context)
            val replyid : Int = sharedCustomPref.getIntValue("values", 0)

            val repliedNotification  =
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.chatapp)
                    .setContentText("Reply Sent").build()
            notificationManager.notify(replyid, repliedNotification)
        }
    }
}