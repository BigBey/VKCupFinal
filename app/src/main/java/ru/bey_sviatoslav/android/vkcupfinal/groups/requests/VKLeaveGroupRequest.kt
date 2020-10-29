package ru.bey_sviatoslav.android.vkcupfinal.groups.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject

class VKLeaveGroupRequest : VKRequest<Int> {
    constructor(groupId: Int) : super("groups.leave"){
        addParam("group_id", groupId)
    }

    override fun parse(r: JSONObject): Int{
        val result = r.getInt("response")
        return result
    }
}