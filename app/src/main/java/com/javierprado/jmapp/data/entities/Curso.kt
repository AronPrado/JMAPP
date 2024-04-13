package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Curso : Serializable {
    var id: String = ""
    var nombre: String = ""
    var nivelEducativo: String = ""
    var dia: String = ""

    var docentes: List<String> = ArrayList()
}