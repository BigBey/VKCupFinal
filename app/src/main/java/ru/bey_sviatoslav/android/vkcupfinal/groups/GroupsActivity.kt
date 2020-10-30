package ru.bey_sviatoslav.android.vkcupfinal.groups

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKApiExecutionException
import dagger.hilt.android.AndroidEntryPoint
import ru.bey_sviatoslav.android.vkcupfinal.R
import ru.bey_sviatoslav.android.vkcupfinal.base.BaseActivity
import ru.bey_sviatoslav.android.vkcupfinal.base.MviViewModel
import ru.bey_sviatoslav.android.vkcupfinal.data.remote.*
import ru.bey_sviatoslav.android.vkcupfinal.groups.adapter.GroupAdapter
import ru.bey_sviatoslav.android.vkcupfinal.utils.*
import ru.bey_sviatoslav.android.vkcupfinal.vo.VKGroup

@AndroidEntryPoint
class GroupsActivity : BaseActivity<GroupsViewState, GroupsIntent>(), PopupMenu.OnMenuItemClickListener {

    override val layoutResourceId: Int
        get() = R.layout.activity_main

    override val viewModel: GroupsViewModel by viewModels()

    private lateinit var groupsRecyclerView: RecyclerView
    private lateinit var groupAdapter : GroupAdapter

    private lateinit var appBarLayout: AppBarLayout
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var uncubscribeTextView: TextView
    private lateinit var extraInfoTextView: TextView

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var titleTextView: TextView
    private lateinit var followersTextView: TextView
    private lateinit var articleTextView: TextView
    private lateinit var newsfeedTextView: TextView
    private lateinit var openButton : Button
    private lateinit var closeImageButton: ImageButton

    private lateinit var bottomLayout: ConstraintLayout
    private lateinit var leaveButton : Button
    private lateinit var countOfGroupsToLeave : TextView

    private lateinit var refreshLayout : SwipeRefreshLayout

    private var vkGroups : ArrayList<VKGroup> =  ArrayList()

    private lateinit var currentGroup: VKGroup

    private var groupScreenNameForBottomSheet = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        initBars()

        initBottomSheetDialog()

        initBottomLayout()

        loginVK()

        initRefreshLayout()
    }

    override fun initViews() {

    }

    override fun render(viewState: GroupsViewState) {
        TODO("Not yet implemented")
    }

    private fun initRecyclerView() {
        groupsRecyclerView = findViewById<RecyclerView>(R.id.groups_recycler_view)
        groupsRecyclerView.setLayoutManager(GridLayoutManager(this, 3))
        groupAdapter = GroupAdapter(this, vkGroups)
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
            R.layout.layout_bottom_sheet,
            findViewById<LinearLayout>(R.id.bottom_sheet_container)
        )

        titleTextView = bottomSheetView.findViewById(R.id.title)
        followersTextView = bottomSheetView.findViewById(R.id.followersTextView)
        articleTextView = bottomSheetView.findViewById(R.id.articleTextView)
        newsfeedTextView = bottomSheetView.findViewById(R.id.newsfeedTextView)

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
            leaveGroups(groupAdapter.getChecked())
            hideBottomLayout()
        }
        countOfGroupsToLeave = findViewById(R.id.count_of_groups_to_leave_text_view)

    }

    internal fun showPopUpWindow(view : View){
        val popupMenu = PopupMenu(this, view)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.inflate(R.menu.pop_up_menu)
        popupMenu.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.about_group -> {
                setGroupScreenNameForLongTap("https://vk.com/${currentGroup.screenName}")
                setTitleText(currentGroup.name)
                VK.execute(VKFriendsInGroupRequest(currentGroup.id), object : VKApiCallback<Int> {
                    override fun fail(error: VKApiExecutionException) {
                        val a = 1
                    }

                    override fun success(result: Int) {
                        setTitleText(
                            if (currentGroup.name.length > 31) currentGroup.name.substring(
                                0,
                                27
                            ) + "..." else currentGroup.name
                        )
                        setFollowersText(currentGroup.membersCount.roundFollowers() + " · " + result.roundFriends())
                        setArticleText(currentGroup.description)
                        VK.execute(VKDateOfLastPostRequest(currentGroup.id), object : VKApiCallback<Int> {
                            override fun fail(error: VKApiExecutionException) {

                            }

                            override fun success(result: Int) {
                                setNewsfeedText("Последняя запись " + result.toLong().toDate())
                            }

                        })
                        showBottomSheetDialog()
                    }


                })
            }
            R.id.albums-> {
                //TODO
            }
            R.id.audios-> {
                onBrowseClick("https://vk.com/audios-${currentGroup.id}")
            }
            R.id.playlists-> {
                onBrowseClick("https://vk.com/audios-${currentGroup.id}?section=playlists")
            }
            R.id.documents-> {
                //TODO
            }
        }
        return true
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
                loadGroups()
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
            VK.login(this, arrayListOf(VKScope.GROUPS, VKScope.WALL))
        }else {
            loadGroups()
        }
    }


    private fun loadGroups(){

        VK.execute(VKGroupsRequest(), object : VKApiCallback<List<VKGroup>> {
            override fun success(result: List<VKGroup>) {
                vkGroups = result as ArrayList<VKGroup>
                initRecyclerView()
            }

            override fun fail(error: VKApiExecutionException) {
                val a = 1
            }
        })
    }

    internal fun setTitleText(text: String){
        titleTextView.setText(text)
    }

    internal fun setCurrentGroup(currentVKGroup: VKGroup){
        this.currentGroup = currentVKGroup
    }

    internal fun setFollowersText(text: String){
        followersTextView.setText(text)
    }

    internal fun setArticleText(text: String){
        articleTextView.setText(text)
    }

    internal fun setNewsfeedText(text: String){
        newsfeedTextView.setText(text)
    }

    internal fun showBottomSheetDialog() : Boolean{
        bottomSheetDialog.show()
        return true
    }

    internal fun setGroupScreenNameForLongTap(string: String){
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

    private fun leaveGroups(groupsToLeave: Array<Int?>){
        var k = 0
        for((index, group) in groupsToLeave.withIndex()){
            if(group != null){
                VK.execute(VKLeaveGroupRequest(group), object : VKApiCallback<Int> {
                    override fun success(result: Int) {
                        if (result == 1)
                            Log.d(TAG, "group with id = $group with index $index was left")
                    }

                    override fun fail(error: VKApiExecutionException) {
                        Log.d(TAG, "group with id = $group with index $index wasn't left")
                    }
                })
                try {
                    groupAdapter.deleteItem(index - k)
                    groupAdapter.notifyItemRemoved(index - k)
                    groupAdapter.notifyItemRangeChanged(index - k, vkGroups.size)
                    groupAdapter.notifyDataSetChanged()
                    k++
                }catch (e: IndexOutOfBoundsException){
                    Log.d(TAG, "IndexOutOfBoundsException at ${group}")
                }
            }
        }
        groupAdapter.setChecked(vkGroups.size)
        groupAdapter.setCheckedCount(0)
        groupAdapter.notifyDataSetChanged()
    }

    private fun initRefreshLayout(){
        refreshLayout = findViewById(R.id.refresh_layout)
        refreshLayout.setOnRefreshListener {
            loadGroups()
        }
    }

    companion object {
        private val TAG = "MainActivity"
    }
}
