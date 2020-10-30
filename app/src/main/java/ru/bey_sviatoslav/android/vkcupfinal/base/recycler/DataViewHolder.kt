package com.example.onto.base.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class DataViewHolder<T>(
    view: View
) : RecyclerView.ViewHolder(view) {
    abstract fun bindData(data: T)
}