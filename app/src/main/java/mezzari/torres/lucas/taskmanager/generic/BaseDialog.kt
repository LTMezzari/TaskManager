package mezzari.torres.lucas.taskmanager.generic

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment

/**
 * @author Lucas T. Mezzari
 * @since 16/09/2020
 */
abstract class BaseDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        setupDialog(dialog)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        setupDialog(dialog)
    }

    protected open fun setupDialog(dialog: Dialog?) {
        dialog?.run {
            window?.setGravity(Gravity.CENTER)
            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        }
    }
}