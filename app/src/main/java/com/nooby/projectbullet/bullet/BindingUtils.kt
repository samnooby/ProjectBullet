package com.nooby.projectbullet.bullet

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.nooby.projectbullet.R
import com.nooby.projectbullet.database.Bullet
import com.nooby.projectbullet.database.BulletType

//Binds the image to the current bullet type
@BindingAdapter("bulletImage")
fun ImageView.setBulletImage(item: Bullet?) {
    item?.let {
        setImageResource(when (item.bulletType) {
            BulletType.NOTE -> R.drawable.bullet_icon_note
            BulletType.INCOMPLETETASK -> R.drawable.bullet_icon_task
            BulletType.COMPLETETASK -> R.drawable.bullet_icon_complete
            BulletType.EVENT -> R.drawable.bullet_icon_event
            else -> R.drawable.bullet_icon_note
        })
    }
}

//Binds the text to the bullets message field
@BindingAdapter("bulletText")
fun TextView.setMessageText(item: Bullet?) {
    item?.let {
        text = item.message
    }
}