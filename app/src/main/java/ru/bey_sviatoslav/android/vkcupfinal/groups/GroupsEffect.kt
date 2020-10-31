package ru.bey_sviatoslav.android.vkcupfinal.groups

import com.vk.api.sdk.VK
import ru.bey_sviatoslav.android.vkcupfinal.base.MviEffect
import ru.bey_sviatoslav.android.vkcupfinal.vo.VKGroup

sealed class GroupsEffect : MviEffect{

    object VKLoginEffect : GroupsEffect()

    object InitialLoadingEffect : GroupsEffect()

    data class InitialLoadingErrorEffect(
        val throwable: Throwable
    ) : GroupsEffect()

    data class GroupsLoadedEffect(
        val groups: List<VKGroup>
    ) : GroupsEffect()

    object RefreshLoadingEffect : GroupsEffect()

    data class RefreshLoadingErrorEffect(
        val throwable: Throwable
    ) : GroupsEffect()

    data class LeavingGroupsEffect(
        val canDeleteAllSelected: Boolean,
        val groupsForDelete: List<VKGroup>
    ): GroupsEffect()

    data class GroupMenuEffect(
        val groupMenuAction: GroupMenuAction,
        val group: VKGroup?
    ): GroupsEffect()

    data class GroupInfoEffect(
        val group: VKGroup,
        val groupMenuAction: GroupMenuAction,
        val countOfFriendsInGroup: Int?,
        val dateOfLastPost: Int?
    ): GroupsEffect()
}