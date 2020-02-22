package com.crimson.mvvm.utils

import android.R
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.annotation.NonNull
import com.crimson.mvvm.ext.appContext
import com.crimson.mvvm.ext.logw


/**
 *
 * 键盘工具
 *
 */
object KeyboardUtils {

    private const val TAG_ON_GLOBAL_LAYOUT_LISTENER = -8

    /**
     * Show the soft input.
     */
    fun showSoftInput(@NonNull activity: Activity) {
        if (!isSoftInputVisible(activity)) {
            toggleSoftInput()
        }
    }

    /**
     * Show the soft input.
     *
     * @param view The view.
     */
    fun showSoftInput(@NonNull view: View) {
        showSoftInput(view, 0)
    }

    /**
     * Show the soft input.
     *
     * @param view  The view.
     * @param flags Provides additional operating flags.  Currently may be
     * 0 or have the [InputMethodManager.SHOW_IMPLICIT] bit set.
     */
    fun showSoftInput(@NonNull view: View, flags: Int) {
        val imm: InputMethodManager? =
            appContext()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                ?: return
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
        imm?.showSoftInput(view, flags, object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                if (resultCode == InputMethodManager.RESULT_UNCHANGED_HIDDEN
                    || resultCode == InputMethodManager.RESULT_HIDDEN
                ) {
                    toggleSoftInput()
                }
            }
        })
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    /**
     * Hide the soft input.
     *
     * @param activity The activity.
     */
    fun hideSoftInput(@NonNull activity: Activity) {
        var view: View? = activity.currentFocus
        if (view == null) {
            val decorView: View = activity.window.decorView
            val focusView: View = decorView.findViewWithTag("keyboardTagView")
            if (focusView == null) {
                view = EditText(activity)
                view.setTag("keyboardTagView")
                (decorView as ViewGroup).addView(view, 0, 0)
            } else {
                view = focusView
            }
            view.requestFocus()
        }
        hideSoftInput(view)
    }

    /**
     * Hide the soft input.
     *
     * @param view The view.
     */
    fun hideSoftInput(@NonNull view: View) {
        val imm: InputMethodManager =
            appContext()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                ?: return
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Toggle the soft input display or not.
     */
    fun toggleSoftInput() {
        val imm: InputMethodManager =
            appContext()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                ?: return
        imm.toggleSoftInput(0, 0)
    }

    private var sDecorViewDelta = 0

    /**
     * Return whether soft input is visible.
     *
     * @param activity The activity.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isSoftInputVisible(@NonNull activity: Activity): Boolean {
        return getDecorViewInvisibleHeight(activity.window) > 0
    }

    private fun getDecorViewInvisibleHeight(@NonNull window: Window): Int {
        val decorView: View = window.getDecorView()
        val outRect = Rect()
        decorView.getWindowVisibleDisplayFrame(outRect)
        logw(
            "getDecorViewInvisibleHeight: "
                    + (decorView.getBottom() - outRect.bottom)
        )
        val delta: Int = Math.abs(decorView.getBottom() - outRect.bottom)
        if (delta <= getNavBarHeight() + getStatusBarHeight()) {
            sDecorViewDelta = delta
            return 0
        }
        return delta - sDecorViewDelta
    }

    /**
     * Register soft input changed listener.
     *
     * @param activity The activity.
     * @param listener The soft input changed listener.
     */
    fun registerSoftInputChangedListener(
        @NonNull activity: Activity,
        @NonNull listener: OnSoftInputChangedListener
    ) {
        registerSoftInputChangedListener(activity.window, listener)
    }

    /**
     * Register soft input changed listener.
     *
     * @param window   The window.
     * @param listener The soft input changed listener.
     */
    fun registerSoftInputChangedListener(
        @NonNull window: Window,
        @NonNull listener: OnSoftInputChangedListener
    ) {
        val flags: Int = window.getAttributes().flags
        if (flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS != 0) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        val contentView: FrameLayout = window.findViewById(R.id.content)
        val decorViewInvisibleHeightPre = intArrayOf(getDecorViewInvisibleHeight(window))
        val onGlobalLayoutListener = OnGlobalLayoutListener {
            val height = getDecorViewInvisibleHeight(window)
            if (decorViewInvisibleHeightPre[0] != height) {
                listener.onSoftInputChanged(height)
                decorViewInvisibleHeightPre[0] = height
            }
        }
        contentView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
        contentView.setTag(TAG_ON_GLOBAL_LAYOUT_LISTENER, onGlobalLayoutListener)
    }

    /**
     * Unregister soft input changed listener.
     *
     * @param window The window.
     */
    fun unregisterSoftInputChangedListener(@NonNull window: Window) {
        val contentView: FrameLayout = window.findViewById(R.id.content)
        val tag = contentView.getTag(TAG_ON_GLOBAL_LAYOUT_LISTENER)
        if (tag is OnGlobalLayoutListener) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                contentView.viewTreeObserver
                    .removeOnGlobalLayoutListener(tag)
            }
        }
    }

    /**
     * Fix the bug of 5497 in Android.
     *
     * Don't set adjustResize
     *
     * @param activity The activity.
     */
    fun fixAndroidBug5497(@NonNull activity: Activity) {
        fixAndroidBug5497(activity.window)
    }

    /**
     * Fix the bug of 5497 in Android.
     *
     * Don't set adjustResize
     *
     * @param window The window.
     */
    fun fixAndroidBug5497(@NonNull window: Window) { //        int softInputMode = window.getAttributes().softInputMode;
//        window.setSoftInputMode(softInputMode & ~WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        val contentView: FrameLayout = window.findViewById(R.id.content)
        val contentViewChild: View = contentView.getChildAt(0)
        val paddingBottom: Int = contentViewChild.getPaddingBottom()
        val contentViewInvisibleHeightPre5497 =
            intArrayOf(getContentViewInvisibleHeight(window))
        contentView.viewTreeObserver
            .addOnGlobalLayoutListener {
                val height = getContentViewInvisibleHeight(window)
                if (contentViewInvisibleHeightPre5497[0] != height) {
                    contentViewChild.setPadding(
                        contentViewChild.getPaddingLeft(),
                        contentViewChild.getPaddingTop(),
                        contentViewChild.getPaddingRight(),
                        paddingBottom + getDecorViewInvisibleHeight(window)
                    )
                    contentViewInvisibleHeightPre5497[0] = height
                }
            }
    }

    private fun getContentViewInvisibleHeight(window: Window): Int {
        val contentView: View = window.findViewById(R.id.content) ?: return 0
        val outRect = Rect()
        contentView.getWindowVisibleDisplayFrame(outRect)
        logw(
            "getContentViewInvisibleHeight: "
                    + (contentView.getBottom() - outRect.bottom)
        )
        val delta: Int = Math.abs(contentView.getBottom() - outRect.bottom)
        return if (delta <= getStatusBarHeight() + getNavBarHeight()) {
            0
        } else delta
    }


    private fun getStatusBarHeight(): Int {
        val resources: Resources = appContext()?.resources?:return 0
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    private fun getNavBarHeight(): Int {
        val res: Resources = appContext()?.resources?:return 0
        val resourceId: Int = res.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId != 0) {
            res.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    private fun getActivityByView(@NonNull view: View): Activity? {
        return getActivityByContext(view.getContext())
    }

    private fun getActivityByContext(context: Context): Activity? {
        var context: Context = context
        if (context is Activity) return context
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = (context as ContextWrapper).baseContext
        }
        return null
    }

///////////////////////////////////////////////////////////////////////////
// interface
///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
// interface
///////////////////////////////////////////////////////////////////////////
    interface OnSoftInputChangedListener {
        fun onSoftInputChanged(height: Int)
    }

}
