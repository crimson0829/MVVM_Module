package com.crimson.mvvm.net.throwable

import java.io.IOException



class NoConnectionException(private val customMessage: String? = null) : IOException() {

    override val message: String?
        get() = customMessage ?: "No Internet Connection"
}