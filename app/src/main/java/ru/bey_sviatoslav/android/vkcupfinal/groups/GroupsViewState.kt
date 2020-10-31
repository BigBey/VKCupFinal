package ru.bey_sviatoslav.android.vkcupfinal.groups

import ru.bey_sviatoslav.android.vkcupfinal.base.MviViewState
import ru.bey_sviatoslav.android.vkcupfinal.vo.VKGroup

data class GroupsViewState(
    val isLoggedIn: Boolean,
    val isInitialLoading: Boolean,
    val initialError: Throwable?,
    val groups: List<VKGroup>,
    val isRefreshLoading: Boolean,
    val refreshError: Throwable?,
    val groupMenuAction: GroupMenuAction,
    val currentGroup: VKGroup?,
    val countOfFriendsInGroup: Int?,
    val dateOfLastPost: Int?,
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
            currentGroup = null,
            countOfFriendsInGroup = null,
            dateOfLastPost = null,
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
            currentGroup = null,
            countOfFriendsInGroup = null,
            dateOfLastPost = null,
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
            currentGroup = null,
            countOfFriendsInGroup = null,
            dateOfLastPost = null,
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
            currentGroup = null,
            countOfFriendsInGroup = null,
            dateOfLastPost = null,
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
            currentGroup = null,
            countOfFriendsInGroup = null,
            dateOfLastPost = null,
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
            currentGroup = null,
            countOfFriendsInGroup = null,
            dateOfLastPost = null,
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
            currentGroup = null,
            countOfFriendsInGroup = null,
            dateOfLastPost = null,
            canDeleteAllSelected = null,
            groupsForDelete = emptyList()
        )

        fun groupMenuState(
            groups: List<VKGroup>,
            groupMenuAction: GroupMenuAction,
            currentGroup: VKGroup?
        ): GroupsViewState = GroupsViewState(
            isLoggedIn = true,
            isInitialLoading = false,
            initialError = null,
            groups = groups,
            isRefreshLoading = false,
            refreshError = null,
            groupMenuAction = groupMenuAction,
            currentGroup = currentGroup,
            countOfFriendsInGroup = null,
            dateOfLastPost = null,
            canDeleteAllSelected = null,
            groupsForDelete = emptyList()
        )

        fun groupInfoState(
            groups: List<VKGroup>,
            groupMenuAction: GroupMenuAction,
            currentGroup: VKGroup?,
            countOfFriendsInGroup: Int?,
            dateOfLastPost: Int?
        ): GroupsViewState = GroupsViewState(
            isLoggedIn = true,
            isInitialLoading = false,
            initialError = null,
            groups = groups,
            isRefreshLoading = false,
            refreshError = null,
            groupMenuAction = groupMenuAction,
            currentGroup = currentGroup,
            countOfFriendsInGroup = countOfFriendsInGroup,
            dateOfLastPost = dateOfLastPost,
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
            currentGroup = null,
            countOfFriendsInGroup = null,
            dateOfLastPost = null,
            canDeleteAllSelected = canDeleteAllSelected,
            groupsForDelete = groupsForDelete
        )
    }
}