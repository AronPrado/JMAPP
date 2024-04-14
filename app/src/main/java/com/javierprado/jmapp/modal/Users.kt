package com.javierprado.jmapp.modal

import android.os.Parcel
import android.os.Parcelable
data class Users (
    val userid: String? = "",
    val info: String? = "",
    val correo : String? ="",
    val estado : String? = "",
    val tipo : String? = "",//DOC, APOD
    val tipoid : String? = "",//docenteId, apderadoId
    val token : String? = "",
    ): Parcelable{
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
        )
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(userid)
            parcel.writeString(info)
            parcel.writeString(correo)
            parcel.writeString(estado)
            parcel.writeString(tipo)
            parcel.writeString(tipoid)
            parcel.writeString(token)
        }
        override fun describeContents(): Int {
            return 0
        }
        companion object CREATOR : Parcelable.Creator<Users> {
            override fun createFromParcel(parcel: Parcel): Users {
                return Users(parcel)
            }
            override fun newArray(size: Int): Array<Users?> {
                return arrayOfNulls(size)
            }
        }
    }