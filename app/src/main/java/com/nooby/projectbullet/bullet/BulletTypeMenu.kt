package com.nooby.projectbullet.bullet

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.lifecycle.MutableLiveData
import com.nooby.projectbullet.R
import com.nooby.projectbullet.database.BulletType

class BulletTypeMenu(context: Context): PopupWindow(context) {

    val currentBulletType = MutableLiveData<BulletType>()
    val currentBulletImg = MutableLiveData<Int>()

    init {
        contentView  = LayoutInflater.from(context).inflate(R.layout.bullet_type_menu, null)
        isFocusable = true
        setupButtons()
    }

    private fun setupButtons() {
        //Sets the type and image for each button click
        var currentButton: ImageView = contentView.findViewById(R.id.btn_bullet_type_note)
        currentButton.setOnClickListener {
            currentBulletType.value = BulletType.NOTE
            currentBulletImg.value = R.drawable.bullet_icon_note
            dismiss()
        }
        currentButton = contentView.findViewById(R.id.btn_bullet_type_task)
        currentButton.setOnClickListener {
            currentBulletType.value = BulletType.INCOMPLETETASK
            currentBulletImg.value = R.drawable.bullet_icon_task
            dismiss()
        }
        currentButton = contentView.findViewById(R.id.btn_bullet_type_complete)
        currentButton.setOnClickListener {
            currentBulletType.value = BulletType.COMPLETETASK
            currentBulletImg.value = R.drawable.bullet_icon_complete
            dismiss()
        }
        currentButton = contentView.findViewById(R.id.btn_bullet_type_event)
        currentButton.setOnClickListener {
            currentBulletType.value = BulletType.EVENT
            currentBulletImg.value = R.drawable.bullet_icon_event
            dismiss()
        }
    }
}