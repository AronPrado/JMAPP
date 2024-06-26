package com.javierprado.jmapp.view.activities.comunicacion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.modal.Users
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.activities.menus.MenuDocenteActivity

@Suppress("DEPRECATION")
class ChatDocenteApoderadoActivity : AppCompatActivity() {
    var token = ""
    private lateinit var navController: NavController
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore

    // LISTAS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_docente_apoderado)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

//        generateToken()
    }
//    fun generateToken(){
//        val firebaseInstance = FirebaseInstallations.getInstance()
//        firebaseInstance.id.addOnSuccessListener{installationid->
//            FirebaseMessaging.getInstance().token.addOnSuccessListener { gettoken->
//                token = gettoken
//                val hasHamp = hashMapOf<String, Any>("token" to token)
//                firestore.collection("Tokens").document(AnotherUtil.getUidLoggedIn()).set(hasHamp)
//            }
//        }.addOnFailureListener {}
//    }
    override fun onPause() {
        super.onPause()
        if (auth.currentUser!=null){
            firestore.collection("Users").document(AnotherUtil.getUidLoggedIn()).update("estado", "Desconectado")
        }
    }
    override fun onStart() {
        super.onStart()
        if (auth.currentUser!=null){
            firestore.collection("Users").document(AnotherUtil.getUidLoggedIn()).update("estado", "En linea")
        }
    }
//    @Deprecated("Deprecated in Java")
//    override fun onBackPressed() {
//        if (supportFragmentManager.backStackEntryCount > 0) {
//            val intent = Intent(this@ChatDocenteApoderadoActivity, MenuDocenteActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//            startActivity(intent)
//            super.onBackPressed()
//        } else {
//            // If we are on the Home fragment, exit the app
//            if (navController.currentDestination?.id == R.id.seleccionarUsuarioFragment) {
//                moveTaskToBack(true)
//            } else {
//                super.onBackPressed()
//            }
//        }
//    }
}