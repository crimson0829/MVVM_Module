package com.crimson.mvvm.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.crimson.mvvm.ext.appContext
import com.crimson.mvvm.ext.isNotNull


/**
 *
 * 剪切板工具
 */
object ClipboardUtils {

    /**
     * 复制文本到剪贴板
     * 如果text为空，则表示清空剪切板
     *
     * @param text 文本
     */
    fun copyText(text: String?) {
        val cm: ClipboardManager? = getManager()
        cm?.setPrimaryClip(ClipData.newPlainText(null, text))
    }

    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    fun getText(): String? {
        val cm: ClipboardManager? = getManager()
        val clip: ClipData? = cm?.primaryClip
        return if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text
            if (text.isNotNull) {
                return text.toString()
            }
            return null
        } else null
    }

    /**
     * 复制uri到剪贴板
     *
     * @param uri uri
     */
    fun copyUri(uri: Uri?) {
        val cm: ClipboardManager? = getManager()
        cm?.setPrimaryClip(ClipData.newUri(appContext()?.contentResolver, "uri", uri))
    }

    /**
     * 获取剪贴板的uri
     *
     * @return 剪贴板的uri
     */
    fun getUri(): Uri? {
        val cm: ClipboardManager? = getManager()
        val clip: ClipData? = cm?.primaryClip
        return if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).uri
        } else null
    }

    /**
     * 复制意图到剪贴板
     *
     * @param intent 意图
     */
    fun copyIntent(intent: Intent?) {
        val cm: ClipboardManager? = getManager()
        cm?.setPrimaryClip(ClipData.newIntent("intent", intent))
    }

    /**
     * 获取剪贴板的意图
     *
     * @return 剪贴板的意图
     */
    fun getIntent(): Intent? {
        val cm: ClipboardManager? = getManager()
        val clip: ClipData? = cm?.primaryClip
        return if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).intent
        } else null
    }

    private fun getManager(): ClipboardManager? {
        return appContext()?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    }
}