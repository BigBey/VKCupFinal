package ru.bey_sviatoslav.android.vkcupfinal.groups.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.exceptions.VKApiExecutionException
import ru.bey_sviatoslav.android.vkcupfinal.R
import ru.bey_sviatoslav.android.vkcupfinal.groups.GroupsActivity
import ru.bey_sviatoslav.android.vkcupfinal.groups.models.VKGroup
import ru.bey_sviatoslav.android.vkcupfinal.groups.requests.VKDateOfLastPostRequest
import ru.bey_sviatoslav.android.vkcupfinal.groups.requests.VKFriendsInGroupRequest
import ru.bey_sviatoslav.android.vkcupfinal.groups.utils.roundFollowers
import ru.bey_sviatoslav.android.vkcupfinal.groups.utils.roundFriends
import ru.bey_sviatoslav.android.vkcupfinal.groups.utils.toDate


//Унаследуем наш класс GroupAdapter от класса RecyclerView.Adapter
//Тут же указываем наш собственный ViewHolder, который предоставит нам доступ к view компонентам
class GroupAdapter(
    private val activity: GroupsActivity,
    private var groups: MutableList<VKGroup>,
    private var checked: Array<Int?> = arrayOfNulls(groups.size),
    private var checkedCount: Int = 0
) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent?.context).inflate(R.layout.group_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = groups.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vkGroup = groups.get(position)
        holder.itemView.isLongClickable = true
        holder.bind(vkGroup, position)
        if (checkedCount > 0)
            activity.showBottomLayout()
        else
            activity.hideBottomLayout()
        holder.itemView.setOnLongClickListener {
            /*activity.setGroupScreenNameForLongTap("https://vk.com/${vkGroup.screenName}")
            activity.setTitleText(vkGroup.name)
            VK.execute(VKFriendsInGroupRequest(vkGroup.id), object : VKApiCallback<Int> {
                override fun fail(error: VKApiExecutionException) {
                    val a = 1
                }

                override fun success(result: Int) {
                    activity.setTitleText(
                        if (vkGroup.name.length > 31) vkGroup.name.substring(
                            0,
                            27
                        ) + "..." else vkGroup.name
                    )
                    activity.setFollowersText(vkGroup.membersCount.roundFollowers() + " · " + result.roundFriends())
                    activity.setArticleText(vkGroup.description)
                    VK.execute(VKDateOfLastPostRequest(vkGroup.id), object : VKApiCallback<Int> {
                        override fun fail(error: VKApiExecutionException) {

                        }

                        override fun success(result: Int) {
                            activity.setNewsfeedText("Последняя запись " + result.toLong().toDate())
                        }

                    })
                    activity.showBottomSheetDialog()
                }


            })*/
            activity.setCurrentGroup(vkGroup)
            activity.showPopUpWindow(holder.itemView)
            return@setOnLongClickListener true
        }
        holder.itemView.setOnClickListener {
            if (checked[position] == null) {
                checked[position] = vkGroup.id
                checkedCount++
            } else {
                checked[position] = null
                checkedCount--
            }
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var groupIconImageView: ImageView? = null
        var checkedRoundImageView: ImageView? = null
        var checkedIconImageView: ImageView? = null

        init {
            groupIconImageView = itemView!!.findViewById(R.id.group_icon_image_view)
            checkedRoundImageView = itemView!!.findViewById(R.id.checked_round_image_view)
            checkedIconImageView = itemView!!.findViewById(R.id.checked_image_view)
        }

        fun bind(vkGroup: VKGroup, position: Int) {
            Glide.with(activity).load(vkGroup.photo).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(groupIconImageView!!)
            if (checked[position] == null) {
                checkedRoundImageView!!.visibility = View.GONE
                checkedIconImageView!!.visibility = View.GONE
            } else {
                checkedRoundImageView!!.visibility = View.VISIBLE
                checkedIconImageView!!.visibility = View.VISIBLE
            }
            if (checkedCount > 0) {

            }
        }
    }

    internal fun getChecked(): Array<Int?> {
        return checked
    }

    internal fun setChecked(size: Int) {
        this.checked = arrayOfNulls(size)
    }

    internal fun setCheckedCount(count: Int) {
        this.checkedCount = 0
    }

    internal fun deleteItem(position: Int) {
        groups.removeAt(position)
    }
}