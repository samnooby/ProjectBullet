package com.nooby.projectbullet.bullet

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import com.nooby.projectbullet.R
import com.nooby.projectbullet.database.Bullet
import java.lang.ClassCastException
import java.lang.IllegalStateException

class BulletNoteEditMenu(val bullet: Bullet, val notePosition: Int, val note: String): DialogFragment() {
    var editText = note
    var isDelete = false

    private lateinit var listener: EditNoteListener
    private lateinit var newNoteText: EditText

    interface EditNoteListener {
        fun onDialogClose(dialog: BulletNoteEditMenu)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { frag ->
            val builder = AlertDialog.Builder(frag)
            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.note_edit_view, null)
            newNoteText = view.findViewById(R.id.edit_note_txt)
            newNoteText.setText(editText)
            newNoteText.setOnKeyListener { v, keyCode, event ->
                when {
                    ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) -> {
                        dismiss()
                        return@setOnKeyListener true
                    }
                    else -> false
                }
            }
            view.findViewById<ImageView>(R.id.delete_note_btn).setOnClickListener {
                isDelete = true
                dismiss()
            }

            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = targetFragment as EditNoteListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$targetFragment must implement EditNoteListener")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        editText = newNoteText.text.toString()
        listener.onDialogClose(this)
    }
}