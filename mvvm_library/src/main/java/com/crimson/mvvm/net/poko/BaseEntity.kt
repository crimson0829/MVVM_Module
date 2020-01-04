package com.crimson.mvvm.net.poko

/**
 * @author crimson
 * @date   2019-12-24
 *
 */
open class BaseEntity<T>  {

    val code: Int? = 0
    val result: String? = ""
    val message: String? = ""
    val data: T? = null
    val list: MutableList<T>? = arrayListOf()

}