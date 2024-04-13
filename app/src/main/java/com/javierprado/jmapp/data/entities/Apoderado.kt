package com.javierprado.jmapp.data.entities

import java.io.Serializable


 class Apoderado : Serializable  {
     var id: String  = ""
     var nombres: String = ""
     var apellidos: String = ""

     var correo: String = ""
     var telefono: Int = 0
     var direccion: String = ""
     var usuarioId: String = ""

     var estudiantes: List<String> = ArrayList()

     constructor()
     constructor(
         nombres: String,
         apellidos: String,
         correo: String,
         telefono: Int,
         direccion: String,
         estudiantes: List<String>
     ) {
         this.nombres = nombres
         this.apellidos = apellidos
         this.correo = correo
         this.telefono = telefono
         this.direccion = direccion
         this.estudiantes = estudiantes
     }

     constructor(correo: String, telefono: Int, direccion: String) {
         this.correo = correo
         this.telefono = telefono
         this.direccion = direccion
     }
 }