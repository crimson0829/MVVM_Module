package com.crimson.mvvm.utils

import java.io.Closeable
import java.io.IOException

/**
 * @author crimson
 * @date   2019-12-26
 */
object IOUtils {
    /**
     * 关闭IO
     *
     * @param closeables closeables
     */
    fun closeIO(vararg closeables: Closeable?) {
        for (closeable in closeables) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}