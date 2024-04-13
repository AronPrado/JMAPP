package com.javierprado.jmapp.data.entities

import java.io.Serializable

class UserValid: Serializable {
    var kind: String = ""
    var localId: String = ""
    var email: String = ""
    var displayName: String = ""
    var idToken:String = ""
    var registered: Boolean = true
}