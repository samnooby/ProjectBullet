package com.nooby.projectbullet.daily

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nooby.projectbullet.database.Bullet
import com.nooby.projectbullet.database.Day

@BindingAdapter("dayText")
fun TextView.setMessageText(item: Day) {
    item?.let {
        text = item.name
    }
}

