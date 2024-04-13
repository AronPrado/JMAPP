package com.javierprado.jmapp.data.entities

import java.io.Serializable

class UserAuth: Serializable {
    var email: String = ""
    var password: String = ""
    constructor()
    constructor(email: String, password: String) : this() {
        this.email = email
        this.password = password
    }
}