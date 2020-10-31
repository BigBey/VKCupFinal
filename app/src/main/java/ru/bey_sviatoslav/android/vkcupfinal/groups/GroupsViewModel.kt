package ru.bey_sviatoslav.android.vkcupfinal.groups

import androidx.hilt.lifecycle.ViewModelInject
import ru.bey_sviatoslav.android.vkcupfinal.base.BaseViewModel
import ru.bey_sviatoslav.android.vkcupfinal.utils.Result
import ru.bey_sviatoslav.android.vkcupfinal.vo.VKGroup

class GroupsViewModel @ViewModelInject constructor(val repository: RemoteGroupsRepository) : BaseViewModel<GroupsViewState, GroupsEffect, GroupsIntent, GroupsAction>(){
    override fun initialState(): GroupsViewState = GroupsViewState.initialState

    override fun intentInterpreter(intent: GroupsIntent): GroupsAction =
        when(intent){
            is GroupsIntent.VKLoginIntent -> GroupsAction.VKLoginAction
            is GroupsIntent.InitialIntent -> GroupsAction.LoadGroupsAction
            is GroupsIntent.ReloadIntent -> GroupsAction.LoadGroupsAction
            is GroupsIntent.RefreshIntent -> GroupsAction.RefreshGroupsAction
            is GroupsIntent.LeaveGroupsIntent -> GroupsAction.LeaveGroupsAction(intent.groupsForDelete)
            is GroupsIntent.GroupMenuIntent -> GroupsAction.OpenGroupMenuAction
            is GroupsIntent.GroupInfoIntent -> GroupsAction.OpenGroupInfoAction(intent.group)
            is GroupsIntent.GroupAlbumsIntent -> GroupsAction.OpenGroupAlbumsAction(intent.group)
            is GroupsIntent.GroupAudiosIntent -> GroupsAction.OpenGroupAudiosAction(intent.group)
            is GroupsIntent.GroupPlaylistsIntent -> GroupsAction.OpenGroupPlaylistsAction(intent.group)
            is GroupsIntent.GroupDocumentsIntent -> GroupsAction.OpenGroupDocumentsAction(intent.group)

        }


    override suspend fun performAction(action: GroupsAction): GroupsEffect =
        when (action) {
            is GroupsAction.VKLoginAction -> {
                addIntermediateEffect(GroupsEffect.InitialLoadingEffect)
                GroupsEffect.VKLoginEffect
            }
            is GroupsAction.LoadGroupsAction -> {
                addIntermediateEffect(GroupsEffect.InitialLoadingEffect)
                when (val result = repository.getGroups()){
                    is Result.Success -> GroupsEffect.GroupsLoadedEffect(result.data)
                    is Result.Error -> GroupsEffect.InitialLoadingErrorEffect(result.throwable)
                }
            }
            is GroupsAction.RefreshGroupsAction -> {
                addIntermediateEffect(GroupsEffect.RefreshLoadingEffect)
                when (val result = repository.getGroups()){
                    is Result.Success -> GroupsEffect.GroupsLoadedEffect(result.data)
                    is Result.Error -> GroupsEffect.RefreshLoadingErrorEffect(result.throwable)
                }
            }
            is GroupsAction.LeaveGroupsAction -> {
                val groupsForDelete = ArrayList<VKGroup>()
                for(group in action.groupsForDelete){
                    when (val result = repository.leaveGroup(group.id)){
                        is Result.Success -> {
                            if(result.data == 1){
                                groupsForDelete.add(group)
                            }
                        }
                        is Result.Error -> {
                            break
                        }
                    }
                }
                if (groupsForDelete.size == action.groupsForDelete.size) {
                    GroupsEffect.LeavingGroupsEffect(true, groupsForDelete)
                }else{
                    GroupsEffect.LeavingGroupsEffect(false, groupsForDelete)
                }
            }
            is GroupsAction.OpenGroupMenuAction -> GroupsEffect.GroupMenuEffect(GroupMenuAction.OPEN_GROUP_MENU, null)
            is GroupsAction.OpenGroupInfoAction -> {
                var countOfFriendsInGroup: Int?  = null
                var dateOfLastPost: Int?  = null
                countOfFriendsInGroup = when (val result = repository.getFriendsInGroup(action.group.id)){
                    is Result.Success -> {
                        result.data
                    }
                    is Result.Error -> {
                        null
                    }
                }
                dateOfLastPost = when (val result = repository.getFriendsInGroup(action.group.id)){
                    is Result.Success -> {
                        result.data
                    }
                    is Result.Error -> {
                        null
                    }
                }
                if (countOfFriendsInGroup == null || dateOfLastPost == null){
                    GroupsEffect.GroupInfoEffect(action.group, GroupMenuAction.OPEN_GROUP_INFO, null, null)
                }else{
                    GroupsEffect.GroupInfoEffect(action.group, GroupMenuAction.OPEN_GROUP_INFO, countOfFriendsInGroup, dateOfLastPost)
                }
            }
            is GroupsAction.OpenGroupAlbumsAction -> GroupsEffect.GroupMenuEffect(GroupMenuAction.OPEN_GROUP_ALBUMS, action.group)
            is GroupsAction.OpenGroupAudiosAction -> GroupsEffect.GroupMenuEffect(GroupMenuAction.OPEN_GROUP_AUDIOS, action.group)
            is GroupsAction.OpenGroupPlaylistsAction -> GroupsEffect.GroupMenuEffect(GroupMenuAction.OPEN_GROUP_PLAYLISTS, action.group)
            is GroupsAction.OpenGroupDocumentsAction -> GroupsEffect.GroupMenuEffect(GroupMenuAction.OPEN_GROUP_DOCUMENTS, action.group)
        }


    override fun stateReducer(oldState: GroupsViewState, effect: GroupsEffect): GroupsViewState =
        when(effect){
            is GroupsEffect.VKLoginEffect -> GroupsViewState.vkLoginState
            is GroupsEffect.InitialLoadingEffect -> GroupsViewState.initialLoadingState
            is GroupsEffect.InitialLoadingErrorEffect -> GroupsViewState.initialErrorState(
                effect.throwable
            )
            is GroupsEffect.GroupsLoadedEffect -> GroupsViewState.groupsLoadedState(effect.groups)
            is GroupsEffect.RefreshLoadingEffect -> GroupsViewState.refreshLoadingState(oldState.groups)
            is GroupsEffect.RefreshLoadingErrorEffect -> GroupsViewState.refreshLoadingErrorState(oldState.groups, effect.throwable)
            is GroupsEffect.LeavingGroupsEffect -> {
                val remainingGroups = oldState.groups as ArrayList
                for(group in effect.groupsForDelete){
                    remainingGroups.remove(group)
                }
                GroupsViewState.leaveGroupsState(remainingGroups, effect.canDeleteAllSelected, effect.groupsForDelete)
            }
            is GroupsEffect.GroupMenuEffect -> GroupsViewState.groupMenuState(oldState.groups, effect.groupMenuAction, effect.group)
            is GroupsEffect.GroupInfoEffect -> GroupsViewState.groupInfoState(oldState.groups, effect.groupMenuAction, effect.group, effect.countOfFriendsInGroup, effect.dateOfLastPost)
        }
}