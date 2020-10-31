package ru.bey_sviatoslav.android.vkcupfinal.groups

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.onto.base.recycler.RecyclerState
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import dagger.hilt.android.AndroidEntryPoint
import ru.bey_sviatoslav.android.vkcupfinal.R
import ru.bey_sviatoslav.android.vkcupfinal.base.BaseActivity
import ru.bey_sviatoslav.android.vkcupfinal.groups.adapter.GroupAdapterV
import ru.bey_sviatoslav.android.vkcupfinal.utils.roundFollowers
import ru.bey_sviatoslav.android.vkcupfinal.utils.roundFriends
import ru.bey_sviatoslav.android.vkcupfinal.utils.toDate
import ru.bey_sviatoslav.android.vkcupfinal.vo.VKGroup
import ru.bey_sviatoslav.android.vkcupfinal.groups.GroupMenuAction.*
import ru.bey_sviatoslav.android.vkcupfinal.groups.adapter.GroupViewHolder
import ru.bey_sviatoslav.android.vkcupfinal.sharing.SharePostActivity

@AndroidEntryPoint
class GroupsActivity : BaseActivity<GroupsViewState, GroupsIntent>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_groups

    override val viewModel: GroupsViewModel by viewModels()

    private val intentLiveData = MutableLiveData<GroupsIntent>().also { intents ->
        intents.value = GroupsIntent.VKLoginIntent
        _intentLiveData.addSource(intents) {
            _intentLiveData.value = it
        }
    }

    private lateinit var groupsRecyclerView: RecyclerView
    private lateinit var groupAdapter : GroupAdapterV

    private lateinit var appBarLayout: AppBarLayout
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var uncubscribeTextView: TextView
    private lateinit var extraInfoTextView: TextView

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var titleTextView: TextView
    private lateinit var followersTextView: TextView
    private lateinit var articleTextView: TextView
    private lateinit var newsfeedTextView: TextView
    private lateinit var shareButton : Button
    private lateinit var openButton : Button
    private lateinit var closeImageButton: ImageButton

    private lateinit var bottomLayout: ConstraintLayout
    private lateinit var leaveButton : Button
    private lateinit var countOfGroupsToLeave : TextView

    private lateinit var refreshLayout : SwipeRefreshLayout

    private var vkGroupsForDelete : ArrayList<VKGroup> =  ArrayList()

    private var groupScreenNameForBottomSheet = ""

    override fun initViews() {
        supportActionBar?.hide()

        initBars()

        initBottomSheetDialog()

        initBottomLayout()

        initRefreshLayout()

        initRecyclerView()
    }

    override fun render(viewState: GroupsViewState) {
        val state = when {
            !viewState.isLoggedIn -> {
                loginVK()
                RecyclerState.LOADING
            }
            viewState.isInitialLoading -> RecyclerState.LOADING
            viewState.initialError != null -> RecyclerState.ERROR
            viewState.groups.isEmpty() -> RecyclerState.EMPTY
            viewState.groupsForDelete.isNotEmpty() -> {
                if(viewState.canDeleteAllSelected!!){
                    Toast.makeText(this, "К сожалению не удалось отписаться от всех сообществ:(", Toast.LENGTH_LONG)
                }
                for(i in 0 until groupsRecyclerView.childCount){
                    val viewHolder = groupsRecyclerView.getChildViewHolder(groupsRecyclerView.getChildAt(i)) as GroupViewHolder
                    viewHolder.setIsChecked(false)
                }
                groupAdapter.notifyDataSetChanged()
                RecyclerState.ITEM
            }
            else -> RecyclerState.ITEM
        }

        when (viewState.groupMenuAction){
            OPEN_GROUP_INFO -> {
                setGroupScreenNameForLongTap("https://vk.com/${viewState.currentGroup!!.screenName}")
                setTitleText(viewState.currentGroup.name)
                setTitleText(
                    if (viewState.currentGroup.name.length > 31) viewState.currentGroup.name.substring(
                        0,
                        27
                    ) + "..." else viewState.currentGroup.name
                )
                setFollowersText(viewState.currentGroup.membersCount.roundFollowers() + " · " + viewState.currentGroup.membersCount.roundFriends())
                setArticleText(viewState.currentGroup.description)
                setNewsfeedText("Последняя запись " + viewState.dateOfLastPost!!.toLong().toDate())
                shareButton.setOnClickListener {
                    val intent = Intent(this@GroupsActivity, SharePostActivity::class.java)
                    intent.putExtra(GROUP_ID, viewState.currentGroup.id)
                    startActivity(intent)
                }
                showBottomSheetDialog()
                RecyclerState.ITEM
            }
            OPEN_GROUP_ALBUMS -> {
                //TODO
            }
            OPEN_GROUP_AUDIOS -> {
                onBrowseClick("https://vk.com/audios-${viewState.currentGroup!!.id}")
            }
            OPEN_GROUP_PLAYLISTS -> {
                onBrowseClick("https://vk.com/audios-${viewState.currentGroup!!.id}?section=playlists")
            }
            OPEN_GROUP_DOCUMENTS -> {
                //TODO
            }
        }

        val isRefreshable = !(viewState.isInitialLoading || viewState.initialError != null)

        refreshLayout.isEnabled = isRefreshable
        refreshLayout.isRefreshing = viewState.isRefreshLoading
        groupAdapter.updateData(viewState.groups, state)
        
    }

    private fun initRecyclerView() {
        groupsRecyclerView = findViewById<RecyclerView>(R.id.groups_recycler_view)
        groupsRecyclerView.setLayoutManager(GridLayoutManager(this, 3))
        groupAdapter = GroupAdapterV(
            onRetry = {
                intentLiveData.value = GroupsIntent.ReloadIntent
            },
            onCLick = { vkGroup, isChecked ->
                if(isChecked) {
                    vkGroupsForDelete.add(vkGroup)
                }else{
                    vkGroupsForDelete.remove(vkGroup)
                }
                groupAdapter.notifyDataSetChanged()
                if(vkGroupsForDelete.isNotEmpty()) {
                    showBottomLayout()
                }else{
                    hideBottomLayout()
                }
            },
            onLongClick = { vkGroup, view ->
                showPopUpWindow(view, vkGroup)
            }
        )
        groupsRecyclerView.adapter = groupAdapter

        groupsRecyclerView.setHasFixedSize(true)
        groupsRecyclerView.setItemViewCacheSize(20)
        groupsRecyclerView.isDrawingCacheEnabled = true

        refreshLayout.isRefreshing = false
    }

    private fun initBars(){
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout)
        appBarLayout = findViewById(R.id.appBarLayout)
        uncubscribeTextView = findViewById(R.id.unsubscribeTextView)
        extraInfoTextView = findViewById(R.id.extraInfoTextView)

        var isShow = true
        var scrollRange = -1
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0) {
                collapsingToolbarLayout.title = resources.getString(R.string.groups)
                isShow = true
            } else if (isShow) {
                collapsingToolbarLayout.title = " "
                isShow = false
            }
        })
    }

    fun initBottomSheetDialog(){
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.layout_bottom_sheet_group_info,
            findViewById<LinearLayout>(R.id.bottom_sheet_container)
        )

        titleTextView = bottomSheetView.findViewById(R.id.title)
        followersTextView = bottomSheetView.findViewById(R.id.followersTextView)
        articleTextView = bottomSheetView.findViewById(R.id.articleTextView)
        newsfeedTextView = bottomSheetView.findViewById(R.id.newsfeedTextView)

        shareButton = bottomSheetView.findViewById<Button>(R.id.shareButton)

        openButton = bottomSheetView.findViewById<Button>(R.id.openButton).apply {
            setOnClickListener {
                onBrowseClick(groupScreenNameForBottomSheet)
            }
        }

        closeImageButton = bottomSheetView.findViewById<ImageButton>(R.id.dismissImageButton).apply {
            setOnClickListener{
                groupScreenNameForBottomSheet = ""
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.setContentView(bottomSheetView)
    }

    private fun initBottomLayout(){
        bottomLayout = findViewById(R.id.bottom_layout)
        leaveButton = findViewById(R.id.leave_button)
        leaveButton.setOnClickListener {
            intentLiveData.value = GroupsIntent.LeaveGroupsIntent(vkGroupsForDelete)
            hideBottomLayout()
        }
        countOfGroupsToLeave = findViewById(R.id.count_of_groups_to_leave_text_view)

    }

    private fun showPopUpWindow(view : View, vkGroup: VKGroup){
        val popupMenu = PopupMenu(this, view)
        popupMenu.setOnMenuItemClickListener{
            when(it!!.itemId){
                R.id.about_group -> {
                    intentLiveData.value = GroupsIntent.GroupInfoIntent(vkGroup)
                }
                R.id.albums-> {
                    intentLiveData.value = GroupsIntent.GroupAlbumsIntent(vkGroup)
                }
                R.id.audios-> {
                    intentLiveData.value = GroupsIntent.GroupAudiosIntent(vkGroup)
                }
                R.id.playlists-> {
                    intentLiveData.value = GroupsIntent.GroupPlaylistsIntent(vkGroup)
                }
                R.id.documents-> {
                    intentLiveData.value = GroupsIntent.GroupDocumentsIntent(vkGroup)
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.inflate(R.menu.pop_up_menu)
        popupMenu.show()
    }

    internal fun showBottomLayout(){
        bottomLayout.visibility = View.VISIBLE
    }

    internal fun hideBottomLayout(){
        bottomLayout.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                intentLiveData.value = GroupsIntent.InitialIntent
            }

            override fun onLoginFailed(errorCode: Int) {
                // User didn't pass authorization
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun loginVK(){
        if(!VK.isLoggedIn()) {
            VK.login(this, arrayListOf(VKScope.GROUPS, VKScope.WALL, VKScope.PHOTOS))
        }else {
            intentLiveData.value = GroupsIntent.InitialIntent
        }
    }

    private fun setTitleText(text: String){
        titleTextView.setText(text)
    }

    private fun setFollowersText(text: String){
        followersTextView.setText(text)
    }

    private fun setArticleText(text: String){
        articleTextView.setText(text)
    }

    private fun setNewsfeedText(text: String){
        newsfeedTextView.setText(text)
    }

    private fun showBottomSheetDialog() : Boolean{
        bottomSheetDialog.show()
        return true
    }

    private fun setGroupScreenNameForLongTap(string: String){
        groupScreenNameForBottomSheet = string
    }

    private fun onBrowseClick(url: String) {
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        // Verify that the intent will resolve to an activity
        if (intent.resolveActivity(packageManager) != null) { // Here we use an intent without a Chooser unlike the next example
            startActivity(intent)
        }
    }

    private fun initRefreshLayout(){
        refreshLayout = findViewById(R.id.refresh_layout)
        refreshLayout.setOnRefreshListener {
            intentLiveData.value = GroupsIntent.RefreshIntent
        }
    }

    companion object {
        private val TAG = "MainActivity"
        const val GROUP_ID = "groupId"
    }
}
