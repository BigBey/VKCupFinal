package ru.bey_sviatoslav.android.vkcupfinal.groups

import ru.bey_sviatoslav.android.vkcupfinal.base.MviViewState
import ru.bey_sviatoslav.android.vkcupfinal.vo.VKGroup

enum class GroupMenuAction{
    DEFAULT,
    OPEN_GROUP_MENU,
    OPEN_GROUP_INFO,
    OPEN_GROUP_ALBUMS,
    OPEN_GROUP_AUDIOS,
    OPEN_GROUP_PLAYLISTS,
    OPEN_GROUP_DOCUMENTS
}

data class GroupsViewState(
    val isLoggedIn: Boolean,
    val isInitialLoading: Boolean,
    val initialError: Throwable?,
    val groups: List<VKGroup>,
    val isRefreshLoading: Boolean,
    val refreshError: Throwable?,
    val groupMenuAction: GroupMenuAction,
    val canDeleteAllSelected: Boolean?,
    val groupsForDelete: List<VKGroup>
) : MviViewState {
    companion object {
        val initialState = GroupsViewState(
            isLoggedIn = true,
            isInitialLoading = false,
            initialError = null,
            groups = emptyList(),
            isRefreshLoading = false,
            refreshError = null,
            groupMenuAction = GroupMenuAction.DEFAULT,
            canDeleteAllSelected = null,
            groupsForDelete = emptyList()
        )

        val vkLoginState = GroupsViewState(
            isLoggedIn = false,
            isInitialLoading = false,
            initialError = null,
            groups = emptyList(),
            isRefreshLoading = false,
            refreshError = null,
            groupMenuAction = GroupMenuAction.DEFAULT,
            canDeleteAllSelected = null,
            groupsForDelete = emptyList()
        )

        val initialLoadingState = GroupsViewState(
            isLoggedIn = true,
            isInitialLoading = true,
            initialError = null,
            groups = emptyList(),
            isRefreshLoading = false,
            refreshError = null,
            groupMenuAction = GroupMenuAction.DEFAULT,
            canDeleteAllSelected = null,
            groupsForDelete = emptyList()
        )

        fun initialErrorState(error: Throwable): GroupsViewState = GroupsViewState(
            isLoggedIn = true,
            isInitialLoading = false,
            initialError = error,
            groups = emptyList(),
            isRefreshLoading = false,
            refreshError = null,
            groupMenuAction = GroupMenuAction.DEFAULT,
            canDeleteAllSelected = null,
            groupsForDelete = emptyList()
        )

        fun groupsLoadedState(groups: List<VKGroup>): GroupsViewState = GroupsViewState(
            isLoggedIn = true,
            isInitialLoading = false,
            initialError = null,
            groups = groups,
            isRefreshLoading = false,
            refreshError = null,
            groupMenuAction = GroupMenuAction.DEFAULT,
            canDeleteAllSelected = null,
            groupsForDelete = emptyList()
        )

        fun refreshLoadingState(groups: List<VKGroup>): GroupsViewState = GroupsViewState(
            isLoggedIn = true,
            isInitialLoading = false,
            initialError = null,
            groups = groups,
            isRefreshLoading = true,
            refreshError = null,
            groupMenuAction = GroupMenuAction.DEFAULT,
            canDeleteAllSelected = null,
            groupsForDelete = emptyList()
        )

        fun refreshLoadingErrorState(
            groups: List<VKGroup>,
            error: Throwable
        ): GroupsViewState = GroupsViewState(
            isLoggedIn = true,
            isInitialLoading = false,
            initialError = null,
            groups = groups,
            isRefreshLoading = false,
            refreshError = error,
            groupMenuAction = GroupMenuAction.DEFAULT,
            canDeleteAllSelected = null,
            groupsForDelete = emptyList()
        )

        fun groupMenuState(
            groups: List<VKGroup>,
            groupMenuAction: GroupMenuAction
        ): GroupsViewState = GroupsViewState(
            isLoggedIn = true,
            isInitialLoading = false,
            initialError = null,
            groups = groups,
            isRefreshLoading = false,
            refreshError = null,
            groupMenuAction = groupMenuAction,
            canDeleteAllSelected = null,
            groupsForDelete = emptyList()
        )

        fun leaveGroupsState(
            groups: List<VKGroup>,
            canDeleteAllSelected: Boolean,
            groupsForDelete: List<VKGroup>
        ): GroupsViewState = GroupsViewState(
            isLoggedIn = true,
            isInitialLoading = false,
            initialError = null,
            groups = groups,
            isRefreshLoading = false,
            refreshError = null,
            groupMenuAction = GroupMenuAction.DEFAULT,
            canDeleteAllSelected = canDeleteAllSelected,
            groupsForDelete = groupsForDelete
        )
    }
}