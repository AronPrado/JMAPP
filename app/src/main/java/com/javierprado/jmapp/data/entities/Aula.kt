package com.javierprado.jmapp.data.entities

import java.io.Serializable


class Aula: Serializable {
    var id: String = ""
    val grado: String = ""
    val nivelEducativo: String = ""
    val seccion: String = ""
    var estudiantes: List<String> = ArrayList()
    var apoderados: List<String> = ArrayList()
    var docentes: List<String> = ArrayList()
    val emailsApoderados: List<String> = ArrayList()

    fun totalEstudiantes(): Int { return estudiantes.size }
}