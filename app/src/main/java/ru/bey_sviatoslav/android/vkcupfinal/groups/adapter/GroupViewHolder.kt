package ru.bey_sviatoslav.android.vkcupfinal.groups.adapter

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.onto.base.recycler.DataViewHolder
import ru.bey_sviatoslav.android.vkcupfinal.R
import ru.bey_sviatoslav.android.vkcupfinal.vo.VKGroup

class GroupViewHolder(
    view: View,
    private val onClick: (VKGroup, Boolean) -> Unit,
    private val onLongClick: (VKGroup,View) -> Unit
) : DataViewHolder<VKGroup>(view) {

    private var isChecked: Boolean = false
    private val groupIconImageView = view.findViewById<ImageView>(R.id.group_icon_image_view)
    private val checkedRoundImageView = view.findViewById<ImageView>(R.id.checked_round_image_view)
    private val checkedIconImageView = view.findViewById<ImageView>(R.id.checked_image_view)

    private lateinit var data: VKGroup

    init {
        view.setOnClickListener {
            isChecked = !isChecked
            onClick(data, isChecked)
        }
        view.setOnLongClickListener {
            onLongClick(data, view)
            return@setOnLongClickListener true
        }
    }

    override fun bindData(data: VKGroup) {
        this.data = data
        Glide.with(groupIconImageView).load(data.photo).diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(groupIconImageView)
        if (!isChecked) {
            checkedRoundImageView!!.visibility = View.GONE
            checkedIconImageView!!.visibility = View.GONE
        } else {
            checkedRoundImageView!!.visibility = View.VISIBLE
            checkedIconImageView!!.visibility = View.VISIBLE
        }
    }

    fun clear() {
        Glide.with(groupIconImageView).clear(groupIconImageView)
    }

    fun setIsChecked(isChecked: Boolean){
        this.isChecked = isChecked
    }
}