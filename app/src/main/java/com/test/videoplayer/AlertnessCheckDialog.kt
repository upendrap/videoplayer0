package com.test.videoplayer

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class AlertnessCheckDialog : DialogFragment() {
    private var listener: DialogInterface.OnClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as DialogInterface.OnClickListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity()).setMessage("You there?")
            .setPositiveButton(android.R.string.yes, listener)
            .setNegativeButton(android.R.string.no, listener)
            .create()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}