package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Tarea(): Serializable {
    var tareaId: Int? = null
    var descripcion: String? = null
    var fechaEntrega: String? = null
    var estado: String? = null
    var itemsCurso: Set<Curso> = HashSet()
    constructor(descripcion: String, estado: String, fechaEntrega: String, itemsCurso: Set<Curso>): this(){
        this.descripcion=descripcion
        this.estado=estado
        this.fechaEntrega=fechaEntrega
        this.itemsCurso=itemsCurso
    }
}