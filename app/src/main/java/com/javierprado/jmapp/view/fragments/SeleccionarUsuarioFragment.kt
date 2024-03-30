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
import com.javierprado.jmapp.data.entities.Apoderado
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
    var aulas: Collection<Aula> = ArrayList()

    private var token = ""
    var usuarioId: Int = 0
    private val retro = RetrofitHelper.getInstanceStatic()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
//        val firebaseAuth = FirebaseAuth.getInstance()
//        logoutimage.setOnClickListener {
//            firebaseAuth.signOut()
//            startActivity(Intent(requireContext(), SignInActivity::class.java))
//        }

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
                val emailsApoderados = aula!!.emailsApoderados
                if(emailsApoderados.isNotEmpty()){
                    viewModel.getUsers(emailsApoderados).observe(viewLifecycleOwner, Observer {
                        adapter.setList(it)
                        rvUsers.adapter = adapter
                        rvSeleccionarUsuarios.visibility=View.GONE
                        rvUsers.visibility=View.VISIBLE
                    })
                }else{
                    Toast.makeText(activity, "No hay docentes.", Toast.LENGTH_SHORT).show()
                }
            }
        })
        hijoAdapter = HijoApoderadoAdapter(ArrayList(), object : onEstudianteApoClickListener {
            override fun onHijoSelected(hijo: Estudiante) {
                api.obtenerDocentes(null, hijo.estudianteId).enqueue(object : Callback<Collection<Docente>> {
                    override fun onResponse(call: Call<Collection<Docente>>, response: Response<Collection<Docente>>) {
                        if (response.isSuccessful) {
                            val emailsDocentes = response.body()!!.map { d-> d.correo!! }
                            viewModel.getUsers(emailsDocentes).observe(viewLifecycleOwner, Observer {
                                if(it.isNotEmpty()){
                                    adapter.setList(it)
                                    rvUsers.adapter = adapter
                                    rvSeleccionarUsuarios.visibility=View.GONE
                                    rvUsers.visibility=View.VISIBLE
                                }else {
                                    Toast.makeText(activity, "No hay docentes.", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }else{
                            msg = "FAIL $msg"
                            Log.e("NR:", msg)
                        }
                    }
                    override fun onFailure(call: Call<Collection<Docente>>, t: Throwable) {
                        Log.e("OBTENER DOCENTES", t.message.toString())
                    }
                } )
            }
        })
        //PRUEBAAAAAAAAAAAAAAAAAAAAAAAAAA
        firestore.collection("Users").document(AnotherUtil.getUidLoggedIn()).addSnapshotListener{ value, error ->
            if (value != null && value.exists()) {
                val user = value.toObject(Users::class.java)
                usuarioId = Integer.valueOf(user!!.tipoid!!)
                token = user.token!!
                retro.setBearerToken(token)
                if(user.tipo!! == RoleType.DOC.name){
                    rvSeleccionarUsuarios.adapter = aulaAdapter
                    api.obtenerAulas(usuarioId).enqueue(object : Callback<Collection<Aula>> {
                        override fun onResponse(call: Call<Collection<Aula>>, response: Response<Collection<Aula>>) {
                            msg = response.headers()["message"] ?: ""
                            if (response.isSuccessful) {
                                aulas = response.body()!!
                                aulaAdapter.setAulas(aulas as MutableList)
                            }else{
                                msg = "FAIL $msg"
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                                Log.e("ERROR:", msg)
                            }
                        }
                        override fun onFailure(call: Call<Collection<Aula>>, t: Throwable) {
                            msg = "Error en la API: ${t.message}"
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                            Log.e("OBTENER AULAS", t.message.toString())
                        }
                    } )
                }else{
                    rvSeleccionarUsuarios.adapter = hijoAdapter
                    api.obtenerApoderado(usuarioId).enqueue(object : Callback<Apoderado> {
                        override fun onResponse(call: Call<Apoderado>, response: Response<Apoderado>) {
                            if (response.isSuccessful) {
                                val estudiantes = response.body()!!.itemsEstudiante!!.toList()
                                hijoAdapter.setEstudiantes(estudiantes)
                            }else{
                                msg = "FAIL $msg"
                                Log.e("NR :", msg)
                            }
                        }
                        override fun onFailure(call: Call<Apoderado>, t: Throwable) {
                            Log.e("OBTENER APODERADOS", t.message.toString())
                        }
                    } )
                }
            }
        }

//        circleImageView.setOnClickListener {
//            view.findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
//        }
        adapter.setOnClickListener(this)
//        viewModel.getRecentUsers().observe(viewLifecycleOwner, Observer {
//            recentadapter.setList(it)
//            rvRecentChats.adapter = recentadapter
//        })

//        recentadapter.setOnChatClickListener(this)
    }
    @Deprecated("Deprecated in Java")
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