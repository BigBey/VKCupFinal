package ru.bey_sviatoslav.android.vkcupfinal.groups.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onto.base.recycler.BaseStateAdapter
import ru.bey_sviatoslav.android.vkcupfinal.R
import ru.bey_sviatoslav.android.vkcupfinal.vo.VKGroup

class GroupAdapterV(
    onRetry: () -> Unit,
    private val onCLick: (VKGroup, Boolean) -> Unit,
    private val onLongClick: (VKGroup, View) -> Unit
): BaseStateAdapter<VKGroup, GroupViewHolder>(onRetry) {
    override fun getDataViewHolder(inflater: LayoutInflater, parent: ViewGroup): GroupViewHolder =
        GroupViewHolder(
            inflater.inflate(R.layout.group_view, parent, false),
            onCLick,
            onLongClick
        )

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is GroupViewHolder) {
            holder.clear()
        }
    }

}