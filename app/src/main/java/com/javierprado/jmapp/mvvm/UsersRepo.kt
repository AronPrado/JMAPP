package com.javierprado.jmapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.modal.Messages
import com.javierprado.jmapp.modal.RecentChats
import com.javierprado.jmapp.modal.Users
import com.javierprado.jmapp.notificaciones.entities.Token
import com.google.firebase.firestore.FirebaseFirestore
class UsersRepo {
    private val firestore = FirebaseFirestore.getInstance()
    fun getUsers(emails: List<String>): LiveData<List<Users>> {
        val users = MutableLiveData<List<Users>>()
        firestore.collection("Users").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }
            val usersList = mutableListOf<Users>()
            snapshot?.documents?.forEach { document ->
                val user = document.toObject(Users::class.java)
                if (user!!.userid != AnotherUtil.getUidLoggedIn() && emails.contains(user.correo)) {
                    user.let {
                        usersList.add(it)
                    }
                }
                users.value = usersList
            }
        }
        return users
    }
}