package com.javierprado.jmapp.data.entities

class AuthResponse {
    var tokenDeAcceso: String
    var tipoDeToken = "Bearer"

    constructor(tokenDeAcceso: String) : super() {
        this.tokenDeAcceso = tokenDeAcceso
    }

    constructor(tokenDeAcceso: String, tipoDeToken: String) : super() {
        this.tokenDeAcceso = tokenDeAcceso
        this.tipoDeToken = tipoDeToken
    }
}