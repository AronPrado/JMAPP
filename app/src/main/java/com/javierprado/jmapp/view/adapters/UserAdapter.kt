package com.javierprado.jmapp.view.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.util.CursoUtil
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.modal.Users
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter : RecyclerView.Adapter<UserHolder>() {
    private var listOfUsers = listOf<Users>()
    private var cursos = listOf<String>()
    private lateinit var tipo: String
    private var listener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lista_user, parent, false)
        return UserHolder(view)
    }

    override fun getItemCount(): Int { return listOfUsers.size }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val users = listOfUsers[position]

        val name = users.info!!.split("\\s".toRegex())[0]
        holder.profileName.text = name

        if (users.estado.equals("En línea")){
            holder.statusImageView.setImageResource(R.drawable.onlinestatus)
        } else {
            holder.statusImageView.setImageResource(R.drawable.offlinestatus)
        }
        if(tipo == RoleType.DOC.name){
            holder.imageProfile.setImageResource(CursoUtil.getImg(cursos[position]))
            holder.imageProfile.setBackgroundColor(CursoUtil.getBackgroundColor(cursos[position]))
//            holder.imageProfile.setCircleBackgroundColorResource(CursoUtil.getBackgroundColor(cursos[position]))
        }else{
            holder.imageProfile.setImageResource(R.drawable.usuario)
        }

//        Glide.with(holder.itemView.context).load(users.tipo).into(holder.imageProfile)

        holder.itemView.setOnClickListener {
            listener?.onUserSelected(position, users)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Users>){
        this.listOfUsers = list
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setTipoUsuarios(tipo: String){
        this.tipo = tipo
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setCursos(cursos: List<String>){
        this.cursos = cursos
        notifyDataSetChanged()
    }
    fun setOnClickListener(listener: OnItemClickListener){
        this.listener = listener
    }
}
class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val profileName: TextView = itemView.findViewById(R.id.userName)
    val imageProfile : CircleImageView = itemView.findViewById(R.id.imageViewUser)
    val statusImageView: ImageView = itemView.findViewById(R.id.statusOnline)
}
interface OnItemClickListener{
    fun onUserSelected(position: Int, users: Users)
}