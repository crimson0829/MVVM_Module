package com.crimson.mvvm_frame

import android.os.Bundle
import com.crimson.mvvm.base.BaseActivity
import com.crimson.mvvm.base.BaseViewModel
import com.crimson.mvvm.binding.bindImage
import com.crimson.mvvm_frame.databinding.ActivityPictureBinding

/**
 * @author crimson
 * @date   2020-01-03
 * 送福利
 */
class PictureActivity : BaseActivity<ActivityPictureBinding,BaseViewModel>() {
    override fun initContentView(savedInstanceState: Bundle?): Int =
        R.layout.activity_picture

    override fun initViewModelId(): Int = 0

    override fun initView() {

        vb?.ivPicture?.bindImage("https://uploadbeta.com/api/pictures/random/?key=%E6%8E%A8%E5%A5%B3%E9%83%8E",
            true,"4",R.drawable.icon_picture)

    }
}