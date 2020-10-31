package ru.bey_sviatoslav.android.vkcupfinal.groups

import ru.bey_sviatoslav.android.vkcupfinal.base.MviIntent
import ru.bey_sviatoslav.android.vkcupfinal.vo.VKGroup

sealed class GroupsIntent : MviIntent{
    object VKLoginIntent: GroupsIntent()

    object InitialIntent: GroupsIntent()

    object ReloadIntent : GroupsIntent()

    object RefreshIntent : GroupsIntent()

    data class LeaveGroupsIntent(val groupsForDelete: List<VKGroup>) : GroupsIntent()

    object GroupMenuIntent : GroupsIntent()

    data class GroupInfoIntent(val group: VKGroup) : GroupsIntent()

    data class GroupAlbumsIntent(val group: VKGroup) : GroupsIntent()

    data class GroupAudiosIntent(val group: VKGroup) : GroupsIntent()

    data class GroupPlaylistsIntent(val group: VKGroup) : GroupsIntent()

    data class GroupDocumentsIntent(val group: VKGroup) : GroupsIntent()

}