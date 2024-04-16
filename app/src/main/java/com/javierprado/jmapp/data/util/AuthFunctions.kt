package com.javierprado.jmapp.data.util

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.StrictMode
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.javierprado.jmapp.BuildConfig
import com.javierprado.jmapp.data.entities.UserAuth
import com.javierprado.jmapp.data.entities.Usuario
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Properties
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class AuthFunctions {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance(FirebaseApp.getInstance())
    fun mostrarCarga(context: Context, text: String):AlertDialog{
        return AlertDialog.Builder(context).setView(ProgressBar(context)).setMessage(text).setCancelable(false).create()
    }
    fun iniciarVerificacionRecaptcha(emailUser: String, passUser: String, rol: String, interfaceActual : AppCompatActivity, nextMenu : AppCompatActivity) {
        val API_SITE_KEY = "6LegfbwpAAAAABeU4KkeropZbw4Vtd8TRzci10YT"
        SafetyNet.getClient(interfaceActual)
            .verifyWithRecaptcha(API_SITE_KEY)
            .addOnSuccessListener(interfaceActual, OnSuccessListener { response ->
                // Indica que la comunicación con el servicio reCAPTCHA fue exitosa
                val userResponseToken = response.tokenResult
                if (response.tokenResult?.isNotEmpty() == true) {
                    // Valida el token de respuesta del usuario utilizando la API de siteverify de reCAPTCHA
//                    validarTokenRecaptcha(userResponseToken)
//                    loginUser(emailUser, passUser, rol, interfaceActual, nextMenu)
                }
            })
            .addOnFailureListener(interfaceActual, OnFailureListener { e ->
                // Maneja los errores de la verificación de reCAPTCHA
                if (e is ApiException) {
                    // Error al comunicarse con el servicio reCAPTCHA
                    Log.e("CAPTCHA", "Error: ${CommonStatusCodes.getStatusCodeString(e.statusCode)}")
                } else {
                    // Otro tipo de error desconocido
                    Log.e("CAPTCHA", "Error: ${e.message}")
                }
            })
    }

    fun loginUser(emailUser: String, passUser: String, rol: String, interfaceActual : AppCompatActivity, nextMenu : AppCompatActivity) {
        val progresDialog = mostrarCarga(interfaceActual, "Iniciando sesión")
        progresDialog.show()
        var msg : String
        //INICIAR SESION
        mAuth.signInWithEmailAndPassword(emailUser, passUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = mAuth.currentUser
                    //OBTENER TOKEN
                    currentUser?.getIdToken(true)?.addOnCompleteListener { idTokenTask ->
                        if (idTokenTask.isSuccessful) {
                            val idToken = idTokenTask.result?.token!!
                            Log.e("TOKEN", idToken)
                            val usuario = Usuario(emailUser, passUser, idToken)
                            RetrofitHelper.getInstanceStatic().getApi().login(usuario, rol).enqueue(object : Callback<Usuario> {
                                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                                    msg = response.headers()["message"] ?: ""
                                    if (response.isSuccessful) {
                                        progresDialog.dismiss()
                                        val user = response.body()!!
                                        val intent = Intent(interfaceActual, nextMenu::class.java)
                                        intent.putExtra(MenuAdministradorActivity().USUARIOID, user.usuarioId)
                                        intent.putExtra(MenuAdministradorActivity().TOKEN, usuario.token)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        interfaceActual.startActivity(intent); interfaceActual.finish()
                                    }else{
                                        Toast.makeText(interfaceActual, msg, Toast.LENGTH_SHORT).show()
                                        Log.e("FIRESTORE", msg)
                                        progresDialog.dismiss()
                                    }
                                }
                                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                                    msg = "Firestore"
                                    Log.e("FIRESTORE", t.message.toString())
                                    Toast.makeText(interfaceActual, msg, Toast.LENGTH_SHORT).show()
                                    progresDialog.dismiss()
                                }
                            })
                        }
                    }
//                    RetroAuth.api.postLoginIdToken(userLog).enqueue(object : Callback<UserValid> {
//                        override fun onResponse(call: Call<UserValid>, response: Response<UserValid>) {
//                            if (response.isSuccessful) {
//                                usuario.token= response.body()!!.idToken
//                                Log.e("GENERADO", usuario.token)
//
//                            }else{
//                                Toast.makeText(interfaceActual, "INVALID SESSION", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                        override fun onFailure(call: Call<UserValid>, t: Throwable) {
//                            msg = "FireAuth2"
//                            Toast.makeText(interfaceActual, msg, Toast.LENGTH_SHORT).show()
//                            Log.e("AUTH2", t.message.toString())
//                        }
//                    })
                }else{
                    progresDialog.dismiss()
                    Toast.makeText(interfaceActual, "Auth1 INCORRECT", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Log.e("AUTH1", e.message.toString())
                Toast.makeText(interfaceActual, "FireAuth1", Toast.LENGTH_SHORT).show()
            }
    }
    fun enviarCredenciales(correoC : String, contrasenaC : String, interfaceActual : AppCompatActivity){
        val correo : String = BuildConfig.CORREO_JMA;
        val contrasena : String = BuildConfig.CONTRASENA_JMA;
        val policy: StrictMode.ThreadPolicy  =  StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val properties = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp-mail.outlook.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.user", correo);
            put("mail.smtp.pwd", contrasena);
        }

        val session = Session.getInstance(properties, null)
        session.debug=true

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(correo))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoC))
                subject = "Credenciales de acceso a la plataforma educativa"
                setContent("<h2>Bienvenido(a) a la plataforma educativa de nuestra institución</h2>"+
                        "<p>A continuación, te proporcionamos las credenciales de acceso:</p>" +
                        "<ul><li><strong>Correo:</strong> $correoC</li>"+
                        "<li><strong>Contraseña:</strong> $contrasenaC</li></ul>"+
                        "<p>Por favor, guarda estas credenciales de forma segura y no las compartas con nadie.</p>",
                    "text/html; charset=utf-8")
            }
            val transport = session.getTransport("smtp")
            transport.connect("smtp-mail.outlook.com", 587, correo, contrasena)
            transport.sendMessage(message, message.allRecipients)
            transport.close()

            Toast.makeText(interfaceActual, "Correo enviado correctamente", Toast.LENGTH_SHORT).show()

        } catch (e: MessagingException) {
            e.printStackTrace()
            Toast.makeText(interfaceActual, "Error al enviar el correo", Toast.LENGTH_SHORT).show()
            Log.e("CORREO", e.message.toString())
        }
    }
}

