package com.crimson.mvvm.ext.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.RelativeLayout.ALIGN_PARENT_END
import android.widget.RelativeLayout.ALIGN_PARENT_START
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.crimson.mvvm.ext.component.dp2px
import com.crimson.mvvm.ext.component.px2dp
import kotlin.math.roundToInt


/**
 * View 扩展函数
 */
//View Ext

fun View.gone(gone: Boolean) = with(this) {
    visibility = when (gone) {
        true -> View.GONE
        false -> View.VISIBLE
    }
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

val View.isVisibile: Boolean
    get() {
        return this.visibility == View.VISIBLE
    }

val View.isGone: Boolean
    get() {
        return this.visibility == View.GONE
    }

val View.isInvisible: Boolean
    get() {
        return this.visibility == View.INVISIBLE
    }

/**
 * INVISIBLE TO VISIBLE AND OTHERWISE
 */
fun View.toggleVisibilityInvisibleToVisible(): View {
    visibility = if (visibility == View.VISIBLE) {
        View.INVISIBLE
    } else {
        View.INVISIBLE
    }
    return this
}

/**
 * INVISIBLE TO GONE AND OTHERWISE
 */
fun View.toggleVisibilityGoneToVisible(): View {
    visibility = if (visibility == View.VISIBLE) {
        View.GONE
    } else {
        View.GONE
    }
    return this
}


fun View.height(height: Int) {
    layoutParams.height = height
    requestLayout()
}

fun View.width(width: Int) {
    layoutParams.width = width
    requestLayout()
}

fun View.size(height: Int, width: Int) {
    layoutParams.width = width
    layoutParams.height = height
    requestLayout()
}

fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
    val alpha = (Color.alpha(color) * factor).roundToInt()
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)
    return Color.argb(alpha, red, green, blue)
}

fun View.setPaddingLeft(value: Int) = setPadding(value, paddingTop, paddingRight, paddingBottom)
fun View.setPaddingRight(value: Int) = setPadding(paddingLeft, paddingTop, value, paddingBottom)
fun View.setPaddingTop(value: Int) = setPaddingRelative(paddingStart, value, paddingEnd, paddingBottom)
fun View.setPaddingBottom(value: Int) = setPaddingRelative(paddingStart, paddingTop, paddingEnd, value)
fun View.setPaddingStart(value: Int) = setPaddingRelative(value, paddingTop, paddingEnd, paddingBottom)
fun View.setPaddingEnd(value: Int) = setPaddingRelative(paddingStart, paddingTop, value, paddingBottom)
fun View.setPaddingHorizontal(value: Int) = setPaddingRelative(value, paddingTop, value, paddingBottom)
fun View.setPaddingVertical(value: Int) = setPaddingRelative(paddingStart, value, paddingEnd, value)


inline var View.bottomMargin: Int
    get():Int {
        return (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
    }
    set(value) {
        (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = value
    }

inline var View.topMargin: Int
    get():Int {
        return (layoutParams as ViewGroup.MarginLayoutParams).topMargin
    }
    set(value) {
        (layoutParams as ViewGroup.MarginLayoutParams).topMargin = value
    }

inline var View.rightMargin: Int
    get():Int {
        return (layoutParams as ViewGroup.MarginLayoutParams).rightMargin
    }
    set(value) {
        (layoutParams as ViewGroup.MarginLayoutParams).rightMargin = value
    }

inline var View.leftMargin: Int
    get():Int {
        return (layoutParams as ViewGroup.MarginLayoutParams).leftMargin
    }
    set(value) {
        (layoutParams as ViewGroup.MarginLayoutParams).leftMargin = value
    }

fun View.setMargin(left: Int, top: Int, right: Int, bottom: Int) {
    (layoutParams as ViewGroup.MarginLayoutParams).setMargins(left, top, right, bottom)
}

inline var View.leftPadding: Int
    get() = paddingLeft
    set(value) = setPadding(value, paddingTop, paddingRight, paddingBottom)

inline var View.topPadding: Int
    get() = paddingTop
    set(value) = setPadding(paddingLeft, value, paddingRight, paddingBottom)

inline var View.rightPadding: Int
    get() = paddingRight
    set(value) = setPadding(paddingLeft, paddingTop, value, paddingBottom)

inline var View.bottomPadding: Int
    get() = paddingBottom
    set(value) = setPadding(paddingLeft, paddingTop, paddingRight, value)


/**
 * View 转 bitmap
 * Get a screenshot of the View, support a long screenshot of the entire RecyclerView list
 * Note: When calling this method, please make sure the View has been measured. If the width and height are 0, an exception will be thrown.
 */
fun View.toBitmap(): Bitmap {
    if (measuredWidth == 0 || measuredHeight == 0) {
        throw RuntimeException("When calling this method, please make sure the View has been measured. If the width and height are 0, an exception is thrown as a reminder！")
    }
    return when (this) {
        is RecyclerView -> {
            this.scrollToPosition(0)
            this.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            val bmp = Bitmap.createBitmap(width, measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)

            //draw default bg, otherwise will be black
            if (background != null) {
                background.setBounds(0, 0, width, measuredHeight)
                background.draw(canvas)
            } else {
                canvas.drawColor(Color.WHITE)
            }
            this.draw(canvas)
            // reset height
            this.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST)
            )
            bmp //return
        }
        else -> {
            val screenshot = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(screenshot)
            if (background != null) {
                background.setBounds(0, 0, width, measuredHeight)
                background.draw(canvas)
            } else {
                canvas.drawColor(Color.WHITE)
            }
            draw(canvas)// Draw the view onto the canvas
            screenshot //return
        }
    }
}


/**
 * fade in 动画
 */
fun View.fadeIn(duration: Long = 500) {
    this.clearAnimation()
    val anim = AlphaAnimation(this.alpha, 1.0f)
    anim.duration = duration
    this.startAnimation(anim)
}

/**
 * fade out 动画
 */
fun View.fadeOut(duration: Long = 500) {
    this.clearAnimation()
    val anim = AlphaAnimation(this.alpha, 0.0f)
    anim.duration = duration
    this.startAnimation(anim)
}


/**
 * inflate 布局
 */
fun ViewGroup.inflate(@LayoutRes id: Int)
        : View = LayoutInflater.from(context).inflate(id, this, false)

fun View.px2dp(pxValue: Float): Float? {
    return context?.px2dp(pxValue)
}

fun View.dp2px(dpValue: Float): Int? {
    return context?.dp2px(dpValue)
}

fun View.dp2px(dpValue: Int): Int? {
    return context?.dp2px(dpValue)
}

fun View.px2dp(pxValue: Int): Float? {
    return context?.px2dp(pxValue)
}


/**
 * Aligns to left of the parent in relative layout
 */
fun View.alignParentStart() {
    val params = layoutParams as RelativeLayout.LayoutParams?

    params?.apply {
        addRule(ALIGN_PARENT_START)
    }

}

/**
 * Aligns to right of the parent in relative layout
 */
fun View.alignParentEnd() {
    val params = layoutParams as RelativeLayout.LayoutParams?

    params?.apply {
        addRule(ALIGN_PARENT_END)
    }

}


/**
 * Aligns in the center of the parent in relative layout
 */
fun View.alignInCenter() {
    val params = layoutParams as RelativeLayout.LayoutParams?

    params?.apply {
        addRule(RelativeLayout.CENTER_HORIZONTAL)
    }

}


/**
 * Sets margins for views in Linear Layout
 */
fun View.linearMargins(left: Int, top: Int, right: Int, bottom: Int) {
    val params = layoutParams as LinearLayout.LayoutParams?

    params?.apply {
        setMargins(left, top, right, bottom)
    }

    this.layoutParams = params

}


/**
 * Sets margins for views in Linear Layout
 */
fun View.linearMargins(size: Int) {
    val params = layoutParams as LinearLayout.LayoutParams?

    params?.apply {
        setMargins(size)
    }
    this.layoutParams = params

}


/**
 * Sets right margin for views in Linear Layout
 */
fun View.endLinearMargin(size: Int) {
    val params = layoutParams as LinearLayout.LayoutParams?

    params?.apply {
        marginEnd = size
    }
    this.layoutParams = params

}


/**
 * Sets bottom margin for views in Linear Layout
 */
fun View.bottomLinearMargin(size: Int) {
    val params = layoutParams as LinearLayout.LayoutParams?

    params?.apply {
        setMargins(marginLeft, marginTop, marginRight, size)
    }
    this.layoutParams = params

}

/**
 * Sets top margin for views in Linear Layout
 */
fun View.topLinearMargin(size: Int) {
    val params = layoutParams as LinearLayout.LayoutParams?

    params?.apply {
        setMargins(marginLeft, size, marginRight, marginBottom)
    }
    this.layoutParams = params

}


/**
 * Sets top margin for views in Linear Layout
 */
fun View.startLinearMargin(size: Int) {
    val params = layoutParams as LinearLayout.LayoutParams?

    params?.apply {
        marginStart = size
    }
    this.layoutParams = params

}


/**
 * Sets margins for views in Relative Layout
 */
fun View.relativeMargins(left: Int, top: Int, right: Int, bottom: Int) {
    val params = layoutParams as RelativeLayout.LayoutParams?

    params?.apply {
        setMargins(left, top, right, bottom)
    }
    this.layoutParams = params

}


/**
 * Sets margins for views in Relative Layout
 */
fun View.relativeMargins(size: Int) {
    val params = layoutParams as RelativeLayout.LayoutParams?

    params?.apply {
        setMargins(size)
    }
    this.layoutParams = params

}


/**
 * Sets right margin for views in Relative Layout
 */
fun View.endRelativeMargin(size: Int) {
    val params = layoutParams as RelativeLayout.LayoutParams?

    params?.apply {
        marginEnd = size
    }
    this.layoutParams = params

}


/**
 * Sets bottom margin for views in Relative Layout
 */
fun View.bottomRelativeMargin(size: Int) {
    val params = layoutParams as RelativeLayout.LayoutParams?

    params?.apply {
        setMargins(marginLeft, marginTop, marginRight, size)
    }
    this.layoutParams = params

}

/**
 * Sets top margin for views in Relative Layout
 */
fun View.topRelativeMargin(size: Int) {
    val params = layoutParams as RelativeLayout.LayoutParams?

    params?.apply {
        setMargins(marginLeft, size, marginRight, marginBottom)
    }
    this.layoutParams = params

}


/**
 * Sets top margin for views in Relative Layout
 */
fun View.startRelativeMargin(size: Int) {
    val params = layoutParams as RelativeLayout.LayoutParams?

    params?.apply {
        marginStart = size
    }
    this.layoutParams = params

}


/**
 * Sets margins for views
 */
fun View.setMargins(size: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams?

    params?.apply {
        setMargins(size)
    }
    this.layoutParams = params

}


/**
 * Sets right margin for views
 */
fun View.endMargin(size: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams?

    params?.apply {
        marginEnd = size
    }
    this.layoutParams = params

}


/**
 * Sets bottom margin for views
 */
fun View.bottomMargin(size: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams?

    params?.apply {
        setMargins(marginLeft, marginTop, marginRight, size)
    }
    this.layoutParams = params

}

/**
 * Sets top margin for views
 */
fun View.topMargin(size: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams?

    params?.apply {
        setMargins(marginLeft, size, marginRight, marginBottom)
    }
    this.layoutParams = params

}


/**
 * Sets top margin for views
 */
fun View.startMargin(size: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams?

    params?.apply {
        marginStart = size
    }
    this.layoutParams = params

}

/**
 * Create a Screnshot of the view and returns it as a Bitmap
 */
fun View.screenshot(): Bitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    draw(canvas)
    canvas.save()
    return bmp
}


/**
 * get Activity On Which View is inflated to
 */
val View.getActivity: Activity?
    get() {
        if (context is Activity)
            return context as Activity
        return null
    }

fun View.margins(
    leftMargin: Int = Int.MAX_VALUE,
    topMargin: Int = Int.MAX_VALUE,
    rightMargin: Int = Int.MAX_VALUE,
    bottomMargin: Int = Int.MAX_VALUE
): View {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    if (leftMargin != Int.MAX_VALUE)
        params.leftMargin = leftMargin
    if (topMargin != Int.MAX_VALUE)
        params.topMargin = topMargin
    if (rightMargin != Int.MAX_VALUE)
        params.rightMargin = rightMargin
    if (bottomMargin != Int.MAX_VALUE)
        params.bottomMargin = bottomMargin
    layoutParams = params
    return this
}


/**
 * Displays a popup by inflating menu with specified
 * [menu resource id][menuResourceId], calling [onClick] when an item
 * is clicked, and optionally calling [onInit] with
 * [PopupMenu] as receiver to initialize prior to display.
 */
fun View.showPopup(
    @MenuRes menuResourceId: Int,
    onInit: PopupMenu.() -> Unit = {},
    onClick: (MenuItem) -> Boolean
) {
    PopupMenu(context, this).apply {
        menuInflater.inflate(menuResourceId, menu)
        onInit(this)
        setOnMenuItemClickListener(onClick)
    }.show()
}

fun View.animateTranslationX(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateTranslationY(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun View.animateTranslationZ(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Z, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateScaleX(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.SCALE_X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateScaleY(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.SCALE_Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateAlpha(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.ALPHA, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateRotation(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.ROTATION, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateRotationX(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.ROTATION_X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateRotationY(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.ROTATION_Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateX(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateY(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun View.animateZ(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.Z, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.translationXAnimator(
    values: FloatArray,
    duration: Long = 300,
    repeatCount: Int = 0,
    repeatMode: Int = 0
): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.translationYAnimator(
    values: FloatArray,
    duration: Long = 300,
    repeatCount: Int = 0,
    repeatMode: Int = 0
): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun View.translationZAnimator(
    values: FloatArray,
    duration: Long = 300,
    repeatCount: Int = 0,
    repeatMode: Int = 0
): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Z, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.scaleXAnimator(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.SCALE_X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.scaleYAnimator(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.SCALE_Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.alphaAnimator(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.ALPHA, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.rotationAnimator(
    values: FloatArray,
    duration: Long = 300,
    repeatCount: Int = 0,
    repeatMode: Int = 0
): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.ROTATION, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.rotationXAnimator(
    values: FloatArray,
    duration: Long = 300,
    repeatCount: Int = 0,
    repeatMode: Int = 0
): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.ROTATION_X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.rotationYAnimator(
    values: FloatArray,
    duration: Long = 300,
    repeatCount: Int = 0,
    repeatMode: Int = 0
): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.ROTATION_Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.xAnimator(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.yAnimator(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun View.zAnimator(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.Z, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.toggleArrow(duration:Long = 200): Boolean {
    return if (rotation == 0f) {
        animate().setDuration(duration).rotation(180f)
        true
    } else {
        animate().setDuration(duration).rotation(0f)
        false
    }
}

@JvmOverloads
fun toggleArrow(show: Boolean, view: View, delay: Boolean = true): Boolean {
    return if (show) {
        view.animate().setDuration((if (delay) 200 else 0).toLong()).rotation(180f)
        true
    } else {
        view.animate().setDuration((if (delay) 200 else 0).toLong()).rotation(0f)
        false
    }
}








