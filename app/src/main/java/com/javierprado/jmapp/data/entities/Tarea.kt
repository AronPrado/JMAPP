package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Tarea(): Serializable {
    var id: String = ""
    var fechaEntrega: String = ""
    var descripcion: String = ""
    var estado: String = ""
    var aulaId: String = ""
    var docenteId: String = ""

    constructor(descripcion: String, fechaEntrega: String, aulaId: String, docenteid: String): this(){
        this.descripcion=descripcion
        this.fechaEntrega=fechaEntrega
        this.aulaId=aulaId
        this.docenteId=docenteid
    }
}