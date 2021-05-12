package com.ongraph.realestate.utils

import android.app.AlertDialog
import android.content.Context
import com.ongraph.realestate.callbacks.AppCallBackListner

object DialogUtils {

    fun showAlertDialog(
        context: Context,
        title: String,
        message: String,
        isShowCancelButton: Boolean,
        dialogCallback: AppCallBackListner.DialogCallback
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("OK") { _, i -> dialogCallback.onClickPositiveButton() }
        if (isShowCancelButton) {
            alertDialogBuilder.setNegativeButton("Cancel") { _, _ ->
                dialogCallback.onClickNegativeButton()
            }
        }
        alertDialogBuilder.show()
//        val alertDialog = alertDialogBuilder.show()
    }

    fun showAlertDialog(
        context: Context,
        message: String,
        dialogCallback: AppCallBackListner.DialogClickCallback?
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("OK") { _, _ -> if (dialogCallback != null) dialogCallback.onButtonClick() }
        alertDialogBuilder.show()
//        val alertDialog = alertDialogBuilder.show()
    }
}