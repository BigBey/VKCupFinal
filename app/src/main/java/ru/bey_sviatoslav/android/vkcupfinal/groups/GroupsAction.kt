package ru.bey_sviatoslav.android.vkcupfinal.groups

import ru.bey_sviatoslav.android.vkcupfinal.base.MviAction
import ru.bey_sviatoslav.android.vkcupfinal.vo.VKGroup

sealed class GroupsAction : MviAction{

    object VKLoginAction : GroupsAction()

    object LoadGroupsAction : GroupsAction()

    object RefreshGroupsAction : GroupsAction()

    data class LeaveGroupsAction(val groupsForDelete: List<VKGroup>) : GroupsAction()

    object OpenGroupMenuAction : GroupsAction()

    data class OpenGroupInfoAction(val groupId: Int) : GroupsAction()

    data class OpenGroupAlbumsAction(val groupId: Int) : GroupsAction()

    data class OpenGroupAudiosAction(val groupId: Int) : GroupsAction()

    data class OpenGroupPlaylistsAction(val groupId: Int) : GroupsAction()

    data class OpenGroupDocumentsAction(val groupId: Int) : GroupsAction()
}