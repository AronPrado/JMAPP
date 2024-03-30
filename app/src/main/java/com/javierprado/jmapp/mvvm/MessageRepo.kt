package com.javierprado.jmapp.mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.view.adapters.RecentChatAdapter
import com.javierprado.jmapp.modal.Messages
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class MessageRepo {
    val firestore = FirebaseFirestore.getInstance()
    fun getMessages(friendid: String): LiveData<List<Messages>> {
        val messages = MutableLiveData<List<Messages>>()

        val uniqueId = listOf(AnotherUtil.getUidLoggedIn(), friendid).sorted()
        uniqueId.joinToString(separator = "")

        firestore.collection("Messages").document(uniqueId.toString()).collection("chats").orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    return@addSnapshotListener
                }
                val messagesList = mutableListOf<Messages>()
                if (!snapshot!!.isEmpty) {
                    snapshot.documents.forEach { document ->
                        val messageModel = document.toObject(Messages::class.java)
                        if (messageModel!!.sender.equals(AnotherUtil.getUidLoggedIn()) && messageModel.receiver.equals(
                                friendid
                            ) ||
                            messageModel.sender.equals(friendid) && messageModel.receiver.equals(
                                AnotherUtil.getUidLoggedIn()
                            )
                        ) {
                            messageModel.let {
                                messagesList.add(it!!)
                            }
                        }
                    }
                    messages.value = messagesList
                }
            }
        return messages
    }
}