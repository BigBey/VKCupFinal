package ru.bey_sviatoslav.android.vkcupfinal.data.remote

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONArray
import org.json.JSONObject

class VKDateOfLastPostRequest(vkGroupId: Int) : VKRequest<Int>("wall.get") {
    init {
        addParam("owner_id", -vkGroupId)
    }

    override fun parse(r: JSONObject): Int {
        val response = r.getJSONObject("response")
        val count = response.getInt("count")
        var result : JSONArray
        if(count > 0){
            result = response.getJSONArray("items")
            return result.getJSONObject(0).getInt("date")
        }
        return -1
    }
}