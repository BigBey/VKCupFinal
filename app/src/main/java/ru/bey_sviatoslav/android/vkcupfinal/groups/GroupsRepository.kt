package ru.bey_sviatoslav.android.vkcupfinal.groups

import android.util.Log
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.exceptions.VKApiExecutionException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.bey_sviatoslav.android.vkcupfinal.data.remote.VKDateOfLastPostRequest
import ru.bey_sviatoslav.android.vkcupfinal.data.remote.VKFriendsInGroupRequest
import ru.bey_sviatoslav.android.vkcupfinal.data.remote.VKGroupsRequest
import ru.bey_sviatoslav.android.vkcupfinal.data.remote.VKLeaveGroupRequest
import ru.bey_sviatoslav.android.vkcupfinal.vo.VKGroup
import ru.bey_sviatoslav.android.vkcupfinal.utils.Result
import ru.bey_sviatoslav.android.vkcupfinal.utils.roundFollowers
import ru.bey_sviatoslav.android.vkcupfinal.utils.roundFriends
import ru.bey_sviatoslav.android.vkcupfinal.utils.toDate
import javax.inject.Inject
import kotlin.coroutines.resume

interface GroupsRepository {
    suspend fun getGroups(): Result<List<VKGroup>>
    suspend fun getDateOfLastPostInGroup(groupId: Int): Result<Int>
    suspend fun getFriendsInGroup(groupId: Int): Result<Int>
    suspend fun leaveGroup(groupId: Int): Result<Int>
}

class RemoteGroupsRepository @Inject constructor() : GroupsRepository {

    private val TAG = "RemoteGroupsRepository"

    override suspend fun getGroups(): Result<List<VKGroup>> =
        suspendCancellableCoroutine { continuation ->
            VK.execute(VKGroupsRequest(), object : VKApiCallback<List<VKGroup>> {
                override fun success(result: List<VKGroup>) {
                    continuation.resume(Result.Success(result))
                }

                override fun fail(error: VKApiExecutionException) {
                    continuation.resume(Result.Error(error))
                }
            })
        }

    override suspend fun getDateOfLastPostInGroup(groupId: Int): Result<Int> =
        suspendCancellableCoroutine { continuation ->
            VK.execute(VKDateOfLastPostRequest(groupId), object : VKApiCallback<Int> {
                override fun success(result: Int) {
                    continuation.resume(Result.Success(result))
                }

                override fun fail(error: VKApiExecutionException) {
                    continuation.resume(Result.Error(error))
                }

            })
        }

    override suspend fun getFriendsInGroup(groupId: Int): Result<Int> =
        suspendCancellableCoroutine { continuation ->
            VK.execute(VKFriendsInGroupRequest(groupId), object : VKApiCallback<Int> {
                override fun success(result: Int) {
                    continuation.resume(Result.Success(result))
                }

                override fun fail(error: VKApiExecutionException) {
                    continuation.resume(Result.Error(error))
                }
            })
        }

    override suspend fun leaveGroup(groupId: Int): Result<Int> =
        suspendCancellableCoroutine { continuation ->
            VK.execute(VKLeaveGroupRequest(groupId), object : VKApiCallback<Int> {
                override fun success(result: Int) {
                    Log.d(TAG, "group with id = $groupId was left")
                    continuation.resume(Result.Success(result))
                }

                override fun fail(error: VKApiExecutionException) {
                    Log.d(TAG, "group with id = $groupId wasn't left")
                    continuation.resume(Result.Error(error))
                }
            })
        }
}


