package com.crimson.mvvm.rx.bus

/**
 * @author crimson
 * @date   2019-09-20
 * rx bus code
 * 统一管理RxBus code,如果有发送的code，就应该根接收的code相对应且最好不要重复
 */
object RxCode {

    /**
     * post标记
     */
    const val POST_CODE = 0xff0001

    /**
     * app是否处于后台
     */
    const val APP_ISBACKGROUND = 0xff0002

    /**
     * 加载错误布局点击
     */
    const val ERROR_LAYOUT_CLICK_CODE = -0xff0001


}