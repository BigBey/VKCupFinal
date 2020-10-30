package ru.bey_sviatoslav.android.vkcupfinal.data.remote

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject

class VKLeaveGroupRequest(groupId: Int) : VKRequest<Int>("groups.leave") {
    init {
        addParam("group_id", groupId)
    }

    override fun parse(r: JSONObject): Int{
        val result = r.getInt("response")
        return result
    }
}