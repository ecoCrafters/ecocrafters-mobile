package com.example.ecocrafters.utils

import android.content.Context
import android.text.Html
import android.widget.Toast
import com.example.ecocrafters.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

fun Context.showToast(msg: String){
    Toast.makeText(
        this,
        msg,
        Toast.LENGTH_SHORT
    ).show()
}

fun String.removeHtmlTag(): String{
    return Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT).toString().replace('￼', ' ')
}

fun showCommentBottomSheetDialog(context: Context, onSendClickListener: (String)-> Unit ) {

    val dialog = BottomSheetDialog(context)

    dialog.setContentView(R.layout.bottom_sheet_comment)

    val btnCancelComment = dialog.findViewById<MaterialButton>(R.id.btn_cancel_comment)
    val btnSendComment = dialog.findViewById<MaterialButton>(R.id.btn_send_comment)
    val edComment = dialog.findViewById<TextInputEditText>(R.id.ed_comment)
    edComment?.requestFocus()

    btnCancelComment?.setOnClickListener {  //handle click event
        dialog.dismiss()

    }
    btnSendComment?.setOnClickListener {
        if (edComment?.text?.isNotBlank() == true){
            onSendClickListener(edComment.text.toString())
            dialog.dismiss()
        } else {
            edComment?.error = context.getString(R.string.komen_kosong)
        }
    }
    dialog.show()
}