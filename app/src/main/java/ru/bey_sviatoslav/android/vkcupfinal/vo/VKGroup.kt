package ru.bey_sviatoslav.android.vkcupfinal.vo

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

data class VKGroup(
    val id: Int = 0,
    val name : String = "",
    val screenName : String = "",
    val photo : String = "",
    val isFavourite : Int,
    val deactivated : String,
    val membersCount : Int = 0,
    val description : String,
    val isMember : Int) : Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(screenName)
        parcel.writeString(photo)
        parcel.writeInt(isFavourite)
        parcel.writeString(deactivated)
        parcel.writeInt(membersCount)
        parcel.writeString(description)
        parcel.writeInt(isMember)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VKGroup> {
        override fun createFromParcel(parcel: Parcel): VKGroup {
            return VKGroup(parcel)
        }

        override fun newArray(size: Int): Array<VKGroup?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject)
                = VKGroup(id = json.optInt("id", 0),
            name = json.optString("name", ""),
            screenName = json.optString("screen_name", ""),
            photo = json.optString("photo_200", ""),
            isFavourite = json.optInt("is_favorite", 0),
            deactivated = json.optString("deactivated", ""),
            membersCount = json.optInt("members_count", 0),
            description = json.optString("description", ""),
            isMember = json.optInt("is_member", 0))
    }
}