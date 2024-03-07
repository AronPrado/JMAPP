package com.javierprado.jmapp.data.entities

import java.io.Serializable


 class Apoderado : Serializable  {
    var apoderadoId: Int? = null
    var nombres: String? = null
    var apellidos: String? = null

    var correo: String? = null
    var telefono: Int? = null
    var direccion: String? = null

    var itemsEstudiante: Set<Estudiante?>? = null

     constructor()
     constructor(
         nombres: String?,
         apellidos: String?,
         correo: String?,
         telefono: Int?,
         direccion: String?,
         itemsEstudiante: HashSet<Estudiante?>
     ) {
         this.nombres = nombres
         this.apellidos = apellidos
         this.correo = correo
         this.telefono = telefono
         this.direccion = direccion
         this.itemsEstudiante = itemsEstudiante
     }

     constructor(correo: String?, telefono: Int?, direccion: String?) {
         this.correo = correo
         this.telefono = telefono
         this.direccion = direccion
     }

 }