package com.nooby.projectbullet.daily

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nooby.projectbullet.database.Bullet

@BindingAdapter("dayText")
fun TextView.setMessageText(item: Day) {
    item?.let {
        text = item.name
    }
}

@BindingAdapter("bulletList")
fun RecyclerView.setItems(bullets: List<Bullet>) {
}