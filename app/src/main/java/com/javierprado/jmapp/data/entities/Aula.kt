package com.javierprado.jmapp.data.entities

import java.io.Serializable


class Aula(): Serializable {
    var estudiantes: Collection<Estudiante> = ArrayList()
    val grado: String? = null
    val seccion: String? = null
    val emailsApoderados: List<String> = ArrayList()

    fun totalEstudiantes(): Int {
        return estudiantes.size
    }
    fun obtenerPadres(estudianteId: Int): List<Apoderado>{
        return estudiantes.find { it.estudianteId == estudianteId }?.itemsApoderado!!.toList()
    }
    fun obtenerEmailsPadresAula(): List<String>{
        val apoderados: MutableList<Apoderado>  = ArrayList()
        estudiantes.forEach { e->if(obtenerPadres(e.estudianteId).isNotEmpty()) apoderados.addAll(obtenerPadres(e.estudianteId)) }
        return apoderados.map { it.correo!! }
    }
}