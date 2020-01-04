package com.crimson.widget.loading

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.crimson.widget.R

/**
 * @author crimson
 * @date   2019-12-30
 * a loading dialog when data loading
 */
class LoadingDialog(
    context: Context, isCancel: Boolean = true
) : Dialog(context,R.style.progress_dialog) {

    private var tvLoading: AppCompatTextView? = null

    init {

        setCancelable(isCancel)
        setContentView(R.layout.widget_loading_dialog)
        window?.setBackgroundDrawable(ColorDrawable(0))
        tvLoading = findViewById(R.id.tv_loading)

        if (isCancel) {
            setOnKeyListener { dialogInterface: DialogInterface, i: Int, _: KeyEvent? ->
                if (i == KeyEvent.KEYCODE_BACK) {
                    dialogInterface.dismiss()
                }
                false
            }
        }


    }


    fun setMessage(message: CharSequence?) :LoadingDialog{
        if (message.isNullOrEmpty()) {
            tvLoading?.visibility = View.GONE
            return this
        }
        tvLoading?.text = message
        return this
    }

    override fun show() {
        if (tvLoading?.text?.toString().isNullOrEmpty()){
            tvLoading?.visibility = View.GONE
        }
        super.show()
    }




}