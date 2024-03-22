package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Aula(): Serializable {
    var estudiantes: Collection<Estudiante> = ArrayList()

    fun totalEstudiantes(): Int {
        return estudiantes.size
    }
    fun obtenerGrado(estudianteId: Int): Int? {
        return estudiantes.find { it.estudianteId == estudianteId }?.grado
    }
    fun obtenerSeccion(estudianteId: Int): String? {
        return estudiantes.find { it.estudianteId == estudianteId }?.seccion
    }

}