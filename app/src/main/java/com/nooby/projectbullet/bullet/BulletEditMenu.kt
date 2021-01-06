package com.nooby.projectbullet.bullet

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import com.nooby.projectbullet.DatePicker
import com.nooby.projectbullet.R
import com.nooby.projectbullet.database.Bullet
import com.nooby.projectbullet.database.BulletType
import kotlinx.android.synthetic.main.bullet_edit_view.*
import java.lang.ClassCastException
import java.lang.IllegalStateException
import java.time.LocalDateTime
import java.util.*

class BulletEditMenu(val bullet: Bullet, private val lifecycleOwner: LifecycleOwner): DialogFragment() {

    var bulletType: BulletType = bullet.bulletType
    var bulletText: String = bullet.message
    var bulletDate: LocalDateTime = bullet.bulletDate
    var deleteBullet: Boolean = false
    var isEdit: Boolean = false

    private lateinit var messageView: EditText
    private lateinit var typeView: ImageView
    private lateinit var listener: EditListener

    interface EditListener {
        fun onDialogClose(dialog: BulletEditMenu)
    }

    //Creates the dialog with the given view
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { frag ->
            val builder = AlertDialog.Builder(frag)
            val inflater = requireActivity().layoutInflater

            //Sets up the view and the default values
            val view = inflater.inflate(R.layout.bullet_edit_view, null)
            messageView = view.findViewById(R.id.edit_txt_bullet)
            messageView.setText(bullet.message)
            typeView = view.findViewById(R.id.edit_bullet_type)
            typeView.setImageResource(when (bullet.bulletType){
                BulletType.NOTE -> R.drawable.bullet_icon_note
                BulletType.INCOMPLETETASK -> R.drawable.bullet_icon_task
                BulletType.COMPLETETASK -> R.drawable.bullet_icon_complete
                BulletType.EVENT -> R.drawable.bullet_icon_event
            })

            //Sets up button listeners
            typeView.setOnClickListener {
                setupPopup().showAsDropDown(typeView)
            }
            //If the user clicks the delete button
            view.findViewById<ImageButton>(R.id.edit_bullet_delete).setOnClickListener {
                //TODO: Add confirmation dialog
                isEdit = true
                deleteBullet = true
                this.dismiss()
            }
            //If the user clicks the cancel button
            view.findViewById<ImageButton>(R.id.edit_cancel).setOnClickListener{
                isEdit = false
                messageView.setText(bullet.message)
                this.dismiss()
            }
            view.findViewById<ImageButton>(R.id.edit_bullet_day).setOnClickListener {
                DatePicker {
                    bulletDate = it.atStartOfDay()
                    isEdit = true
                    Log.i("BulletEditMenu", "Got day $bulletDate")
                }.show(parentFragmentManager, "Day")
            }
            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    //Adds the listener so the bullet can be update once closed
    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = targetFragment as EditListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$targetFragment must impelement editlistener")
        }
    }

    private fun setupPopup(): PopupWindow {
        val window = BulletTypeMenu(requireActivity())

        //Makes the bullet type match the value returned from the popup
        window.currentBulletImg.observe(lifecycleOwner, androidx.lifecycle.Observer {
            it.let {
                typeView.setImageResource(it)
                isEdit = true
            }
        })

        window.currentBulletType.observe(lifecycleOwner, androidx.lifecycle.Observer {
            it.let {
                bulletType = it
                isEdit = true
            }
        })

        return window
    }

    override fun onDestroy() {
        super.onDestroy()
        bulletText = messageView.text.toString()
        if (bulletText != bullet.message) isEdit = true
        if (isEdit) listener.onDialogClose(this)
        Log.i("EditMenu", "Destroyed dialog")
    }

}