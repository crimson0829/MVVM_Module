package com.crimson.mvvm_frame

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.crimson.mvvm.base.BaseActivity
import com.crimson.mvvm.base.BaseViewModel
import com.crimson.mvvm.binding.bindClick
import com.crimson.mvvm.binding.bindImage
import com.crimson.mvvm.binding.consumer.bindConsumer
import com.crimson.mvvm.utils.StatusBarUtils
import com.crimson.mvvm_frame.databinding.ActivityPictureBinding
import kotlinx.android.synthetic.main.activity_picture.*

/**
 * @author crimson
 * @date   2020-01-03
 *
 */
class PictureActivity : BaseActivity<ActivityPictureBinding, BaseViewModel>() {

//    val url = "https://uploadbeta.com/api/pictures/random/?key=%E6%8E%A8%E5%A5%B3%E9%83%8E"
    val url = "https://img.xjh.me/random_img.php?type=bg&ctype=nature&return=302&device=mobile"

    override fun initContentView(savedInstanceState: Bundle?): Int =
        R.layout.activity_picture

    override fun initViewModelId(): Int = 0


    override fun initStatusBar(): Boolean {
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.colorPrimaryDark))
        return true
    }

    override fun initView() {

        btn_change.bindClick(bindConsumer {

            MaterialDialog(context).show {
                listItems(R.array.imageStyles) { _, index, _ ->
                    val style = index + 1
                    bindImage(style)
                    dismiss()
                }
            }

        }, 0)


        bindImage()

    }

    private fun bindImage(style: Int = 2) {
        vb?.ivPicture?.bindImage(
            url,
            style, 5, true, 4, R.drawable.icon_picture
        )
    }

}