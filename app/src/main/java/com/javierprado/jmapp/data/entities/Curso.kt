package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Curso() : Serializable {
    var cursoId: Int = 0
    var nombre: String? = null
    var nivelEducativo: Char? = null
    var dia: String? = null

    var docentes: Collection<Docente> = ArrayList()
}