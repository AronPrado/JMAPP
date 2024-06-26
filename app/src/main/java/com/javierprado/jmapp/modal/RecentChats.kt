package com.javierprado.jmapp.modal

import android.os.Parcel
import android.os.Parcelable
data class RecentChats(val friendid : String? ="",
                       val time : String? = "",
                       val info: String? ="",
                       val sender: String? = "",
                       val message : String? = "",
                       val person: String? = "",
                       val estado: String? ="",
                       ) : Parcelable {
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
        parcel.writeString(friendid)
        parcel.writeString(time)
        parcel.writeString(info)
        parcel.writeString(sender)
        parcel.writeString(message)
        parcel.writeString(person)
        parcel.writeString(estado)
    }
    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<RecentChats> {
        override fun createFromParcel(parcel: Parcel): RecentChats {
            return RecentChats(parcel)
        }
        override fun newArray(size: Int): Array<RecentChats?> {
            return arrayOfNulls(size)
        }
    }
}