package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Curso() : Serializable {
    var cursoId: Int? = null
    var nombre: String? = null
    var nivelEducativo: Char? = null
    var dia: String? = null
}