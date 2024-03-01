package com.javierprado.jmapp.data.util

import android.content.Intent
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.javierprado.jmapp.BuildConfig
import com.javierprado.jmapp.data.entities.AuthResponse
import com.javierprado.jmapp.data.entities.Curso
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.entities.Noticia
import com.javierprado.jmapp.data.entities.Usuario
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.MenuPrincipalApoderadoActivity
import com.javierprado.jmapp.view.menu_administrador
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
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    fun loginUser(emailUser: String, passUser: String, rol: String, interfaceActual : AppCompatActivity, nextMenu : AppCompatActivity) {
        mAuth.signInWithEmailAndPassword(emailUser, passUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val usuario = Usuario(emailUser, passUser)
                    var api: ColegioAPI = RetrofitHelper.getInstanceStatic().getApi()
//                    var retroActual : RetrofitHelper = RetrofitHelper.getInstanceStatic()
                    var msg : String

                    api.login(usuario, rol)?.enqueue(object : Callback<AuthResponse?> {
                        override fun onResponse(call: Call<AuthResponse?>, response: Response<AuthResponse?>) {
                            if (response.isSuccessful) {
                                msg = response.body()?.tokenDeAcceso.toString()
                                val intent = Intent(interfaceActual, nextMenu::class.java)
                                intent.putExtra(menu_administrador().TOKEN, msg)

                                interfaceActual.startActivity(intent)
                                interfaceActual.finish()
                            }else{
                                msg = "No tiene permisos para ingresar."
                            }
                            Toast.makeText(interfaceActual, msg, Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(call: Call<AuthResponse?>, t: Throwable?) {
                            msg = "Error en el API y no en el Firebase."
                            Toast.makeText(interfaceActual, msg, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }.addOnFailureListener { e ->
                Toast.makeText(interfaceActual, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
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
            put("mail.smtp.host", "smtp-mail.outlook.com") // Cambia esto por el servidor SMTP que estés utilizando
            put("mail.smtp.port", "587") // Puerto del servidor SMTP
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
//            e.printStackTrace()
            Toast.makeText(interfaceActual, "Error al enviar el correo", Toast.LENGTH_SHORT).show()
        }
    }
}

