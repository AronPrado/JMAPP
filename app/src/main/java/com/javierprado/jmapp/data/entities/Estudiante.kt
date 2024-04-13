package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Estudiante: Serializable{
    var id: String = ""
    var fechaNacimiento: String = ""
    var nombres: String= ""
    var apellidos: String= ""
    var genero: String= ""
    var dni: Int = 0
    var grado: Int= 0
    var seccion: String= ""
    var nivelEducativo: String= ""
    var estado: String= ""

    var cursos: List<String> = ArrayList()
    var aulaId: String= ""
}