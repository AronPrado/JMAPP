@file:Suppress("DEPRECATION")

package com.javierprado.jmapp.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.view.adapters.*
import com.javierprado.jmapp.modal.RecentChats
import com.javierprado.jmapp.modal.Users
import com.javierprado.jmapp.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.javierprado.jmapp.data.entities.Aula
import com.javierprado.jmapp.data.entities.Docente
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.databinding.FragmentSeleccionarUsuarioBinding
import com.javierprado.jmapp.view.clicks.AulaClick
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val CHANNEL_ID = "my_channel"
class SeleccionarUsuarioFragment : Fragment(), OnItemClickListener, onChatClicked {
    lateinit var rvUsers : RecyclerView
    lateinit var rvSeleccionarUsuarios : RecyclerView
    lateinit var rvRecentChats : RecyclerView
    lateinit var adapter : UserAdapter
    lateinit var aulaAdapter : AulaAdapter
    lateinit var hijoAdapter : HijoApoderadoAdapter
    lateinit var viewModel : ChatAppViewModel
    lateinit var toolbar: Toolbar
    lateinit var circleImageView: CircleImageView
    lateinit var recentadapter : RecentChatAdapter
    lateinit var auth: FirebaseAuth
    lateinit var firestore : FirebaseFirestore
    lateinit var binding: FragmentSeleccionarUsuarioBinding

    private lateinit var msg : String
//    var aulas: List<Aula> = ArrayList()
//    var estudiantes: List<Estudiante> = ArrayList()

    private var token = ""
    var usuarioId: String = ""
    private val retro = RetrofitHelper.getInstanceStatic()
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_seleccionar_usuario, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)
        toolbar = view.findViewById(R.id.toolbarMain)
//        val logoutimage = toolbar.findViewById<ImageView>(R.id.logOut)
//        circleImageView = toolbar.findViewById(R.id.tlImage)
        binding.lifecycleOwner = viewLifecycleOwner
//        viewModel.imageUrl.observe(viewLifecycleOwner, Observer {
//            Glide.with(requireContext()).load(it).into(circleImageView)
//        })
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    getActivity()?.moveTaskToBack(true);
//                    getActivity()?.finish();
//                }
//            })
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        rvUsers = view.findViewById(R.id.rvUsers)
        rvSeleccionarUsuarios = view.findViewById(R.id.rvSeleccionarUsuarios)
        rvRecentChats = view.findViewById(R.id.rvRecentChats)
        adapter = UserAdapter()
        recentadapter = RecentChatAdapter()
        hijoAdapter = HijoApoderadoAdapter()

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val layoutManager2 = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val layoutManager3 = LinearLayoutManager(activity)

        rvUsers.layoutManager = layoutManager
        rvSeleccionarUsuarios.layoutManager = layoutManager2
        rvRecentChats.layoutManager= layoutManager3

        val api = retro.getApi()
        aulaAdapter = AulaAdapter(ArrayList(), object : AulaClick {
            override fun onAulaClicker(aula: Aula?) {
                val emailsApoderados = aula!!.emails
                if(emailsApoderados.isNotEmpty()){
                    viewModel.getUsers(emailsApoderados).observe(viewLifecycleOwner) {
                        adapter.setList(it)
                        adapter.setTipoUsuarios(RoleType.APOD.name)
                        rvUsers.adapter = adapter
                        rvSeleccionarUsuarios.visibility = View.GONE
                        rvUsers.visibility = View.VISIBLE
                    }
                }else{
                    Toast.makeText(activity, "No hay docentes.", Toast.LENGTH_SHORT).show()
                }
            }
        })
        hijoAdapter = HijoApoderadoAdapter(ArrayList(), object : onEstudianteApoClickListener {
            override fun onHijoSelected(hijo: Estudiante) {
                api.listarDocentes("null", hijo.id, "null").enqueue(object : Callback<List<Docente>> {
                    override fun onResponse(call: Call<List<Docente>>, response: Response<List<Docente>>) {
                        msg = response.headers()["message"] ?: ""
                        if (response.isSuccessful) {
                            val emailsDocentes = response.body()!!.map { d-> d.correo }
                            val cursosDocentes = response.body()!!.map { d -> d.cursoId }
                            Log.e("CURSOS", cursosDocentes.toString())
                            viewModel.getUsers(emailsDocentes).observe(viewLifecycleOwner) {
                                adapter.setList(it)
                                adapter.setCursos(cursosDocentes)
                                adapter.setTipoUsuarios(RoleType.DOC.name)
                                rvUsers.adapter = adapter
                                rvSeleccionarUsuarios.visibility = View.GONE
                                rvUsers.visibility = View.VISIBLE
                            }
                        }else{
                            Log.e("NR:", msg)
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<List<Docente>>, t: Throwable) {
                        Log.e("OBTENER DOCENTES", t.message.toString())
                    }
                } )
            }
        })
        firestore.collection("Users").document(AnotherUtil.getUidLoggedIn()).addSnapshotListener{ value, error ->
            if (value != null && value.exists()) {
                val user = value.toObject(Users::class.java)
                usuarioId = user!!.tipoid!! ; token = user.token!! ; retro.setBearerToken(token)
                if(user.tipo!! == RoleType.DOC.name){
                    rvSeleccionarUsuarios.adapter = aulaAdapter
                    api.listarAulas(usuarioId, null, null, null).enqueue(object : Callback<List<Aula>> {
                        override fun onResponse(call: Call<List<Aula>>, response: Response<List<Aula>>) {
                            msg = response.headers()["message"] ?: ""
                            if (response.isSuccessful) {
                                aulaAdapter.setAulas(response.body()!!)
                            }else{
                                Log.e("OBTENER AULAS:", msg)
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<List<Aula>>, t: Throwable) {
                            msg = "Error en la API: ${t.message}"
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                            Log.e("OBTENER AULAS", t.message.toString())
                        }
                    } )
                }else{
                    rvSeleccionarUsuarios.adapter = hijoAdapter
                    api.listarEstudiantes(usuarioId, ArrayList()).enqueue(object : Callback<List<Estudiante>> {
                        override fun onResponse(call: Call<List<Estudiante>>, response: Response<List<Estudiante>>) {
                            msg = response.headers()["message"] ?: ""
                            if (response.isSuccessful) {
                                hijoAdapter.setEstudiantes(response.body()!!)
                            }else{
                                msg = "FAIL $msg"
                                Log.e("NR :", msg)
                            }
                        }
                        override fun onFailure(call: Call<List<Estudiante>>, t: Throwable) {
                            Log.e("OBTENER APODERADOS", t.message.toString())
                        }
                    } )
                }
            }
        }
        adapter.setOnClickListener(this)
        viewModel.getRecentUsers().observe(viewLifecycleOwner, Observer {
            rvRecentChats.adapter = recentadapter
            recentadapter.setList(it)
        })
        recentadapter.setOnChatClickListener(this)

//        circleImageView.setOnClickListener {
//            view.findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
//        }

    }
    //?
    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onActivityCreated(savedInstanceState)",
        "androidx.fragment.app.Fragment"
    ))
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
    override fun onUserSelected(position: Int, users: Users) {
        val action = SeleccionarUsuarioFragmentDirections.actionSeleccionarUsuarioFragmentToChatFragment(users)
        view?.findNavController()?.navigate(action)
    }
    override fun getOnChatCLickedItem(position: Int, chatList: RecentChats) {
        val action = SeleccionarUsuarioFragmentDirections.actionSeleccionarUsuarioFragmentToChatfromHome(chatList)
        view?.findNavController()?.navigate(action)
    }
}