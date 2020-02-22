@file:Suppress("DEPRECATION")

package com.crimson.mvvm.utils

import android.app.Activity
import android.graphics.*
import android.os.Build
import android.view.View
import android.webkit.WebView
import android.widget.ScrollView
import me.jessyan.autosize.utils.LogUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * @author crimson
 * @date 2018/9/14
 * 截图工具类
 */
object CaptureUtils {
    /**
     * 对View进行量测，布局后截图
     * 对webvie不好使，不可见部分为空白
     *
     * @param view
     * @return
     */
    fun convertViewToBitmap(view: View): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            ),
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bitmap = view.drawingCache
        view.destroyDrawingCache()
        return bitmap
    }

    /**
     * 获取整个窗口的截图
     *
     * @param context
     * @return
     */
    fun captureScreen(context: Activity): Bitmap? {
        val cv = context.window.decorView
        cv.isDrawingCacheEnabled = true
        cv.buildDrawingCache()
        val bmp = cv.drawingCache ?: return null
        bmp.setHasAlpha(false)
        bmp.prepareToDraw()
        cv.destroyDrawingCache()
        return bmp
    }

    /**
     * 对单独某个View进行截图
     *
     * @param v
     * @return
     */
    fun buildBitmapFromView(v: View?): Bitmap? {
        if (v == null) {
            return null
        }
        val screenshot: Bitmap
        screenshot = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.RGB_565)
        val c = Canvas(screenshot)
        c.translate(-v.scrollX.toFloat(), -v.scrollY.toFloat())
        v.draw(c)
        c.save()
        c.restore()
        return screenshot
    }

    /**
     * 截取webView快照(webView加载的整个内容的大小)
     *
     * @param webView
     * @return
     */
    fun captureWebView(webView: WebView): Bitmap {
        webView.isDrawingCacheEnabled = true
        webView.buildDrawingCache()
        val snapShot = webView.capturePicture()
        val bmp =
            Bitmap.createBitmap(snapShot.width, snapShot.height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bmp)
        snapShot.draw(canvas)
        canvas.save()
        canvas.restore()
        webView.destroyDrawingCache()
        return bmp
    }

    /**
     * 合并两张bitmap为一张
     *
     * @param background
     * @param foreground
     * @return Bitmap
     */
    fun combineBitmap(background: Bitmap?, foreground: Bitmap): Bitmap? {
        if (background == null) {
            return null
        }
        val bgWidth = background.width
        val bgHeight = background.height
        val fgWidth = foreground.width
        val fgHeight = foreground.height
        val newmap = Bitmap
            .createBitmap(bgWidth, bgHeight, Bitmap.Config.RGB_565)
        val canvas = Canvas(newmap)
        canvas.drawBitmap(background, 0f, 0f, null)
        canvas.drawBitmap(
            foreground, (bgWidth - fgWidth) / 2.toFloat(),
            (bgHeight - fgHeight) / 2.toFloat(), null
        )
        canvas.save()
        canvas.restore()
        return newmap
    }

    /**
     * 得到bitmap的大小
     */
    fun getBitmapSize(bitmap: Bitmap): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //API 19
            bitmap.allocationByteCount
        } else bitmap.rowBytes * bitmap.height
        // 在低版本中用一行的字节x高度
        //earlier version
    }

    /**
     * 截取scrollview的屏幕
     */
    fun getBitmapByScrollView(scrollView: ScrollView): Bitmap? {
        var h = 0
        var bitmap: Bitmap? = null
        for (i in 0 until scrollView.childCount) {
            h += scrollView.getChildAt(i).height
        }
        LogUtils.w("实际高度:$h")
        LogUtils.w(" 高度:" + scrollView.height)
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(
            scrollView.width, h,
            Bitmap.Config.RGB_565
        )
        val canvas = Canvas(bitmap)
        scrollView.draw(canvas)
        canvas.save()
        canvas.restore()
        return bitmap
    }

    /**
     *
     * 获取截取后的长截图
     * @param resource
     */
    fun getClipLongBitmap(resource: Bitmap): Bitmap? {
        try {
            val baos = ByteArrayOutputStream()
            resource.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val isBm: InputStream = ByteArrayInputStream(baos.toByteArray())
            //BitmapRegionDecoder newInstance(InputStream is, boolean isShareable)
//用于创建BitmapRegionDecoder，isBm表示输入流，只有jpeg和png图片才支持这种方式，
// isShareable如果为true，那BitmapRegionDecoder会对输入流保持一个表面的引用，
// 如果为false，那么它将会创建一个输入流的复制，并且一直使用它。即使为true，程序也有可能会创建一个输入流的深度复制。
// 如果图片是逐步解码的，那么为true会降低图片的解码速度。如果路径下的图片不是支持的格式，那就会抛出异常
            val decoder = BitmapRegionDecoder.newInstance(isBm, true)
            val imgWidth = decoder.width
            val imgHeight = decoder.height
            val opts = BitmapFactory.Options()
            //计算图片要被切分成几个整块，
// 如果sum=0 说明图片的长度不足3000px，不进行切分 直接添加
// 如果sum>0 先添加整图，再添加多余的部分，否则多余的部分不足3000时底部会有空白
            val sum = imgHeight / 3000
            val redundant = imgHeight % 3000
            val bitmapList: MutableList<Bitmap> = ArrayList()
            //说明图片的长度 < 3000
            if (sum == 0) { //直接加载
                bitmapList.add(resource)
            } else { //说明需要切分图片
                val rect = Rect()
                for (i in 0 until sum) { //需要注意：mRect.set(left, top, right, bottom)的第四个参数，
//也就是图片的高不能大于这里的4096
                    rect[0, i * 3000, imgWidth] = (i + 1) * 3000
                    val bm = decoder.decodeRegion(rect, opts)
                    bitmapList.add(bm)
                }
                //将多余的不足3000的部分作为尾部拼接
                if (redundant > 0) {
                    rect[0, sum * 3000, imgWidth] = imgHeight
                    val bm = decoder.decodeRegion(rect, opts)
                    bitmapList.add(bm)
                }
            }
            val bigbitmap = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.RGB_565)
            val bigcanvas = Canvas(bigbitmap)
            val paint = Paint()
            var iHeight = 0
            //将之前的bitmap取出来拼接成一个bitmap
            for (i in bitmapList.indices) {
                var bmp: Bitmap? = bitmapList[i]
                bigcanvas.drawBitmap(bmp!!, 0f, iHeight.toFloat(), paint)
                iHeight += bmp.height
                bmp.recycle()
                bmp = null
            }
            return bigbitmap
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}