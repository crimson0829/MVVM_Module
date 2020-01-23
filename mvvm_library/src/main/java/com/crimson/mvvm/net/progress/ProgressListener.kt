package com.crimson.mvvm.net.progress

/**
 * Progress interface
 */
interface ProgressListener {
    fun onStarted()
    fun onFinished()
    // if the content is known it'll return the correct size otherwise you'll see some weird sizes
    fun onUpdate(percent: Int)
}

fun progressListenerDSL(
    downloadStarted: () -> Unit = {},
    downloadFinished: () -> Unit = {},
    progress: (percentage: Int) -> Unit = {}
) = object :
    ProgressListener {
    override fun onStarted() {
        downloadStarted()
    }

    override fun onFinished() {
        downloadFinished()
    }

    override fun onUpdate(percent: Int) {
        progress(percent)
    }
}