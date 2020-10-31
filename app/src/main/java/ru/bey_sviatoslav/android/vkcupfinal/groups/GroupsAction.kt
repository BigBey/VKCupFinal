package ru.bey_sviatoslav.android.vkcupfinal.groups

import ru.bey_sviatoslav.android.vkcupfinal.base.MviAction
import ru.bey_sviatoslav.android.vkcupfinal.vo.VKGroup

sealed class GroupsAction : MviAction{

    object VKLoginAction : GroupsAction()

    object LoadGroupsAction : GroupsAction()

    object RefreshGroupsAction : GroupsAction()

    data class LeaveGroupsAction(val groupsForDelete: List<VKGroup>) : GroupsAction()

    object OpenGroupMenuAction : GroupsAction()

    data class OpenGroupInfoAction(val group: VKGroup) : GroupsAction()

    data class OpenGroupAlbumsAction(val group: VKGroup) : GroupsAction()

    data class OpenGroupAudiosAction(val group: VKGroup) : GroupsAction()

    data class OpenGroupPlaylistsAction(val group: VKGroup) : GroupsAction()

    data class OpenGroupDocumentsAction(val group: VKGroup) : GroupsAction()
}