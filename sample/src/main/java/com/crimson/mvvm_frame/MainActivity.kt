package com.crimson.mvvm_frame

import android.os.Bundle
import com.crimson.mvvm.base.BaseActivity
import com.crimson.mvvm.base.BaseViewModel
import com.crimson.mvvm.binding.consumer.bindConsumer
import com.crimson.mvvm_frame.databinding.ActivityMainBinding


/**
 * @author crimson
 * @date   2019-12-31
 */
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    override fun initViewModelId(): Int {
        return BR.viewModel
    }

    override fun initTitleText(): CharSequence? {
        return "木大木大木大"
    }

}


class MainViewModel : BaseViewModel() {


    val onClickListBtn = bindConsumer<Unit> {
        startActivity(TabActivity::class.java)
    }


    val onClickPictureBtn = bindConsumer<Unit> {
        startActivity(PictureActivity::class.java)

    }


}
