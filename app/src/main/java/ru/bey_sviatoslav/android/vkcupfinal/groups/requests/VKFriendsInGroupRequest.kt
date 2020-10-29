package ru.bey_sviatoslav.android.vkcupfinal.groups.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject

class VKFriendsInGroupRequest : VKRequest<Int> {
    constructor(groupId: Int) : super("groups.getMembers") {
        addParam("group_id", groupId)
        addParam("filter", "friends")
    }

    override fun parse(r: JSONObject): Int {
        return r.getJSONObject("response").getInt("count")
    }
}