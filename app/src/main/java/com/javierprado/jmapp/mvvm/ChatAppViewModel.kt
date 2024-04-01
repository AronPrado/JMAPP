package com.javierprado.jmapp.mvvm

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javierprado.jmapp.data.util.SharedPrefs
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.modal.Messages
import com.javierprado.jmapp.modal.RecentChats
import com.javierprado.jmapp.modal.Users
import com.javierprado.jmapp.notificaciones.entities.NotificationData
import com.javierprado.jmapp.notificaciones.entities.PushNotification
import com.javierprado.jmapp.notificaciones.entities.Token
import com.javierprado.jmapp.notificaciones.network.RetrofitNoti
import com.google.firebase.firestore.FirebaseFirestore
import com.javierprado.jmapp.view.activities.MyApplication
import kotlinx.coroutines.*

class ChatAppViewModel : ViewModel() {
    val message = MutableLiveData<String>()
    val firestore = FirebaseFirestore.getInstance()
    val name = MutableLiveData<String>()
//    val imageUrl = MutableLiveData<String>()

    val usersRepo = UsersRepo()
    val messageRepo = MessageRepo()
    var token: String? = null
    val chatlistRepo = ChatListRepo()

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
    init {
        getCurrentUser()
        getRecentUsers()
    }
    fun getUsers(emails: List<String>): LiveData<List<Users>> {
        return usersRepo.getUsers(emails)
    }
    // sendMessage
    fun sendMessage(sender: String, receiver: String, friendname: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val context = MyApplication.instance.applicationContext
            val hashMap = hashMapOf<String, Any>(
                "sender" to sender,
                "receiver" to receiver,
                "message" to message.value!!,
                "time" to AnotherUtil.getTime()
            )
            val uniqueId = listOf(sender, receiver).sorted()
            uniqueId.joinToString(separator = "")
            val friendnamesplit = friendname.split("\\s".toRegex())[0]
            val mysharedPrefs = SharedPrefs(context)
            mysharedPrefs.setValue("friendid", receiver)
            mysharedPrefs.setValue("chatroomid", uniqueId.toString())
            mysharedPrefs.setValue("friendname", friendnamesplit)
            firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
                .document(AnotherUtil.getTime()).set(hashMap).addOnCompleteListener { taskmessage ->
                    val setHashap = hashMapOf<String, Any>(
                        "friendid" to receiver,
                        "time" to AnotherUtil.getTime(),
                        "sender" to AnotherUtil.getUidLoggedIn(),
                        "message" to message.value!!,
                        "name" to friendname,
                        "person" to "you"
                    )
                    firestore.collection("Conversation${AnotherUtil.getUidLoggedIn()}").document(receiver)
                        .set(setHashap)
                    firestore.collection("Conversation${receiver}").document(AnotherUtil.getUidLoggedIn())
                        .update(
                            "message",
                            message.value!!,
                            "time",
                            AnotherUtil.getTime(),
                            "person",
                            name.value!!
                        )
                    firestore.collection("Tokens").document(receiver).addSnapshotListener { value, error ->
                        if (value != null && value.exists()) {
                            val tokenObject = value.toObject(Token::class.java)
                            token = tokenObject?.token!!
                            val loggedInUsername =
                                mysharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]
                            if (message.value!!.isNotEmpty() && receiver.isNotEmpty()) {
                                PushNotification(
                                    NotificationData(loggedInUsername, message.value!!), token!!
                                ).also {
                                    sendNotification(it)
                                }
                            } else {
                                Log.e("ChatAppViewModel", "NO TOKEN, NO NOTIFICATION")
                            }
                        }
                        Log.e("ViewModel", token.toString())
                        if (taskmessage.isSuccessful){
                            message.value = ""
                        }
                    }
                }
        }
    // getting messages
    fun getMessages(friend: String): LiveData<List<Messages>> {
        return messageRepo.getMessages(friend)
    }
    // get RecentUsers
    fun getRecentUsers(): LiveData<List<RecentChats>> {
        return chatlistRepo.getAllChatList()
    }
    fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            val response = RetrofitNoti.api.postNotificacion(notification)
        } catch (e: Exception) {
            Log.e("ViewModelError", e.toString())
            // showToast(e.message.toString())
        }
    }
    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {
        val context = MyApplication.instance.applicationContext
        firestore.collection("Users").document(AnotherUtil.getUidLoggedIn())
            .addSnapshotListener { value, error ->
                if (value!!.exists()) {
                    val users = value.toObject(Users::class.java)
                    name.value = users?.info!!
//                    imageUrl.value = users.tipo!!

                    val mysharedPrefs = SharedPrefs(context)
                    mysharedPrefs.setValue("username", users.info!!)
                }
            }
    }
    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {
        val context = MyApplication.instance.applicationContext
        val hashMapUser =
            hashMapOf<String, Any>("username" to name.value!!)//, "imageUrl" to imageUrl.value!!
        firestore.collection("Users").document(AnotherUtil.getUidLoggedIn()).update(hashMapUser).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT ).show()
            }
        }
        val mysharedPrefs = SharedPrefs(context)
        val friendid = mysharedPrefs.getValue("friendid")
        //"friendsimage" to imageUrl.value!!
        val hashMapUpdate = hashMapOf<String, Any>("name" to name.value!!, "person" to name.value!!)

        // updating the chatlist and recent list message, image etc
        firestore.collection("Conversation${friendid}").document(AnotherUtil.getUidLoggedIn()).update(hashMapUpdate)
        firestore.collection("Conversation${AnotherUtil.getUidLoggedIn()}").document(friendid!!).update("person", "you")
    }
}