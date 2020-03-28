package com.crimson.mvvm.binding

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.text.*
import android.text.InputFilter.LengthFilter
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import com.crimson.mvvm.binding.consumer.BindConsumer
import com.crimson.mvvm.ext.Api
import com.crimson.mvvm.ext.afterApi
import com.crimson.mvvm.ext.tryCatch
import com.crimson.mvvm.rx.observeOnMainThread
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding3.widget.textChanges
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindUntilEvent
import org.w3c.dom.Text
import java.util.regex.Pattern


/**
 * bind text changes
 * textChanges：绑定文字改变监听
 */
@BindingAdapter("app:textChanges", "app:textChangesError",requireAll = false)
fun TextView.textChanges(
    changesConsumer: BindConsumer<String>?,
    changesErrorConsumer: BindConsumer<Throwable>?=null
) {

    (context as? LifecycleOwner)?.let { owner ->
        textChanges()
            .observeOnMainThread()
            .bindUntilEvent(owner, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                changesConsumer?.accept(it.toString())
            }, {
                changesErrorConsumer?.accept(it)
            })
    }

}

/**
 * bind keyboard search click call back
 * 用户edittext输入确认
 */
@BindingAdapter("app:keyboardSearch")
fun TextView.keyboardSearch(consumer: BindConsumer<Any>?) {

    setOnEditorActionListener { _, actionId, event ->
        if ((actionId == 0 || actionId == 3) && event != null) {
            //点击搜索要做的操作
            consumer?.accept(event)

        }
        return@setOnEditorActionListener false
    }

}

/**
 * 设置图案方位，默认左边
 */
fun TextView.drawable(@DrawableRes drawableRes: Int, direction: Direction = Direction.LEFT) {
    val drawable = ContextCompat.getDrawable(context, drawableRes)
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    when (direction) {
        Direction.LEFT -> setCompoundDrawables(drawable, null, null, null)
        Direction.RIGHT -> setCompoundDrawables(null, null, drawable, null)
        Direction.TOP -> setCompoundDrawables(null, drawable, null, null)
        Direction.BOTTOM -> setCompoundDrawables(null, null, null, drawable)
    }

}

/**
 * 设置颜色
 */
@BindingAdapter("app:bindTextColor")
fun TextView.textColor(@ColorRes colorRes: Int) =
    setTextColor(ContextCompat.getColor(context, colorRes))


/**
 * 方位枚举
 */
enum class Direction {
    LEFT, RIGHT, TOP, BOTTOM
}

/**
 * UnderLine the TextView.
 */
@BindingAdapter("app:underLine")
fun TextView.underLine(boolean: Boolean = true) {
    if (boolean) {
        paint.flags = paint.flags or Paint.UNDERLINE_TEXT_FLAG
        paint.isAntiAlias = true
    } else {
        cancelLine()
    }

}

/**
 * DeleteLine for a TextView.
 */
@BindingAdapter("app:deleteLine")
fun TextView.deleteLine(boolean: Boolean = true) {
    if (boolean) {
        paint.flags = paint.flags or Paint.STRIKE_THRU_TEXT_FLAG
        paint.isAntiAlias = true
    } else {
        cancelLine()
    }

}

//取消划线设置
fun TextView.cancelLine() {
    paint.flags = 0
}


/**
 * Removes the bolding of a text view
 */
fun AppCompatTextView.unBold() {
    paint.isFakeBoldText = false
    paint.isAntiAlias = true
}


fun TextView.setAppereance(resId: Int) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    setTextAppearance(resId)
} else {
    @Suppress("DEPRECATION") setTextAppearance(context, resId)
}


/**
 * Bold the TextView.
 */
fun TextView.bold() {
    paint.isFakeBoldText = true
    paint.isAntiAlias = true
}


/**
 * Set font for TextView.
 */
fun TextView.font(font: String) {
    typeface = Typeface.createFromAsset(context.assets, "fonts/$font.ttf")
}


/**
 * Set different color for substring TextView.
 */
fun TextView.setColorOfSubstring(
    substring: String,
    color: Int
) {
    tryCatch {
        val spannable = SpannableString(text)
        val start = text.indexOf(substring)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, color)),
            start,
            start + substring.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        text = spannable
    }

}

fun TextView.setClickableOfSubstring(
    substring: String,
    color: Int,
    clickConsumer: BindConsumer<View>?,
    underlined: Boolean = false
) {
    tryCatch {
        val spannable = SpannableString(text)
        val start = text.indexOf(substring)
        clickConsumer?.apply {
            spannable.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        clickConsumer.accept(widget)
                    }

                    override fun updateDrawState(ds: TextPaint) {
//                        super.updateDrawState(ds)
                        setTextColor(ContextCompat.getColor(context, color))
                        underLine(underlined)

                    }
                }
                ,
                start,
                start + substring.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        movementMethod = LinkMovementMethod.getInstance();
        text = spannable
        highlightColor = Color.parseColor("#00000000")
    }
}

@BindingAdapter("app:textSubstring", "app:textDrawableRes", requireAll = true)
fun TextView.setImageOfSubstring(substring: String, drawableRes: Int) {

    tryCatch {
        val spannable = SpannableString(text)
        val start = text.indexOf(substring)
        if (start == -1) {
            return
        }

        val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.resources.getDrawable(drawableRes, null)
        } else context.resources.getDrawable(drawableRes)

        val ratio = drawable.intrinsicWidth * 1.0f / drawable.intrinsicHeight

        drawable.setBounds(0, 0, (textSize * ratio).toInt(), textSize.toInt())

        spannable.setSpan(
            ImageSpan(drawable, ImageSpan.ALIGN_BASELINE),
            start,
            start + substring.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        text = spannable
    }

}


/**
 * Set TextView from Html
 */
@BindingAdapter("app:textHtml")
fun TextView.setTextFromHtml(html: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        this.text = Html.fromHtml(html)
    }
}


/**
 * Sets given content to TextView or hides it.
 */
fun TextView.setAsContent(content: CharSequence?) {
    if (!TextUtils.isEmpty(content)) {
        text = content
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}

inline var TextView.isSelectable: Boolean
    get() = isTextSelectable
    set(value) = setTextIsSelectable(value)


fun TextView.updateTextAppearance(@StyleRes resource: Int) =
    TextViewCompat.setTextAppearance(this, resource)

@SuppressLint("RestrictedApi")
fun TextView.textColorAnim(from: Int, to: Int) {
    val textColorAnimator = ObjectAnimator.ofObject(
        this,
        "textColor",
        ArgbEvaluator(),
        ContextCompat.getColor(context, from),
        ContextCompat.getColor(context, to)
    )
    textColorAnimator.duration = 300
    textColorAnimator.start()
}


fun TextView.preComputeCurrentText() {
    val textParams = TextViewCompat.getTextMetricsParams(this)
    val text = PrecomputedTextCompat.create(text, textParams)
    this.text = text
}

@WorkerThread
fun TextView.precomputeSpannableText(text: Spannable): PrecomputedTextCompat {
    val textParams = TextViewCompat.getTextMetricsParams(this)
    return PrecomputedTextCompat.create(text, textParams)
}

@WorkerThread
fun TextView.precomputeText(text: String): PrecomputedTextCompat {
    val textParams = TextViewCompat.getTextMetricsParams(this)
    return PrecomputedTextCompat.create(text, textParams)
}


inline fun TextView.addTextChangedListener(
    crossinline onBeforeTextChanged: (s: CharSequence?, start: Int, count: Int, after: Int) -> Unit = { _, _, _, _ -> },
    crossinline onTextChanged: (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit = { _, _, _, _ -> },
    crossinline onAfterTextChanged: (s: Editable) -> Unit = { }
): TextWatcher {
    val listener = object : TextWatcher {

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
            onTextChanged(s, start, before, count)

        override fun afterTextChanged(s: Editable) = onAfterTextChanged(s)
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) = onBeforeTextChanged(s, start, count, after)

    }

    addTextChangedListener(listener)
    return listener
}

infix fun TextView.set(@StringRes id: Int) {
    setText(id)
}

infix fun TextView.set(text: String?) {
    setText(text)
}

infix fun TextView.set(text: Spannable?) {
    setText(text)
}

infix fun AppCompatTextView.set(text: String?) {
    setPrecomputedText(text)
}


fun TextInputLayout.clearError() {
    error = null
    isErrorEnabled = false
}

val TextView.textString: String
    get() = text.toString()


val AppCompatTextView.textString: String
    get() = text.toString()


fun TextView.setFont(typeface: Typeface?) {
    this.typeface = typeface
}


fun TextView.addTextListener(
    beforeChanged: ((s: CharSequence) -> Unit)? = null,
    onChanged: ((s: CharSequence) -> Unit)? = null,
    afterChanged: ((s: Editable) -> Unit)? = null
) {
    addDebounceTextListener(
        debounceTimeInMillis = 0,
        beforeChanged = beforeChanged, onChanged = onChanged, afterChanged = afterChanged
    )
}

fun TextView.addDebounceTextListener(
    debounceTimeInMillis: Long = 500,
    beforeChanged: ((s: CharSequence) -> Unit)? = null,
    afterChanged: ((s: Editable) -> Unit)? = null,
    onChanged: ((s: CharSequence) -> Unit)? = null
) {
    addTextChangedListener(object : TextWatcher {
        private val changedRunnable: Runnable = Runnable {
            onChanged?.invoke(text)
        }

        override fun afterTextChanged(s: Editable) {
            afterChanged?.invoke(s)
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            beforeChanged?.invoke(s)
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (onChanged == null) return

            if (debounceTimeInMillis <= 0) {
                onChanged.invoke(s)
                return
            }

            removeCallbacks(changedRunnable)
            postDelayed(changedRunnable, debounceTimeInMillis)
        }
    })
}

fun TextView.addDebounceChangeStateListener(
    delayInMillis: Long = 500,
    timeoutInMillis: Long = 0,
    listener: (Boolean) -> Unit
) {
    addTextChangedListener(object : TextWatcher {
        private var start: Boolean = false
        private val runnable: Runnable = Runnable {
            start = false
            listener(false)
            removeCallbacks(timeoutRunnable)

        }

        private val timeoutRunnable: Runnable = object : Runnable {
            override fun run() {
                listener(true)
                postDelayed(this, timeoutInMillis)
            }
        }

        override fun afterTextChanged(s: Editable) {
            //nothing
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //nothing
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (!this.start) {
                this.start = true
                listener(true)
                postDelayed(timeoutRunnable, timeoutInMillis)
            } else {
                if (s.isEmpty()) {
                    listener(false)
                    this.start = false
                    removeCallbacks(timeoutRunnable)
                }
            }
            removeCallbacks(runnable)
            postDelayed(runnable, delayInMillis)
        }
    })
}


fun AppCompatTextView.setPrecomputedText(text: String?) {
    text?.let {
        setTextFuture(PrecomputedTextCompat.getTextFuture(it, this.textMetricsParamsCompat, null))
    }
}

fun TextView.setTextStrikeThru(strikeThru: Boolean) {
    if (strikeThru) setTextStrikeThru() else setTextNotStrikeThru()
}

fun TextView.setTextStrikeThru() {
    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

fun TextView.setTextNotStrikeThru() {
    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
}

fun TextView.setTextUnderlined(underlined: Boolean) {
    if (underlined) setTextUnderlined() else setTextNotUnderlined()
}

fun TextView.setTextUnderlined() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

fun TextView.setTextNotUnderlined() {
    paintFlags = paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
}

fun TextView.setTextOrHide(text: String?) {
    text?.let {
        this.text = it
    } ?: run {
        this.visibility = View.GONE
    }
}

fun AppCompatTextView.setTextOrHide(text: String?) {
    text?.let {
        this.text = it
    } ?: run {
        this.visibility = View.GONE
    }
}


fun TextView.measureText(): Int = paint.measureText(text.toString()).toInt()


/**
 * 禁止输入特殊字符
 */
fun EditText.forbidSpecialCharacter(maxLength: Int = Int.MAX_VALUE) {
    filters = arrayOf(getEditTextInhibitInputSpeChat(), LengthFilter(maxLength))
}

/**
 *
 * 特殊字符过滤
 *
 * @param
 */
private fun getEditTextInhibitInputSpeChat(): InputFilter? {
    return InputFilter { source, start, end, dest, dstart, dend ->
        //                String speChat = "[A-Za-z0-9_\\-\\u4e00-\\u9fa5]+";
//                Pattern pattern = Pattern.compile(speChat);
//                Matcher matcher = pattern.matcher(source.toString());
//                if (matcher.find()) return "";
//                return null;
        val emoji = Pattern.compile(
            "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
            Pattern.UNICODE_CASE or Pattern.CASE_INSENSITIVE
        )
        val emojiMatcher = emoji.matcher(source)
        if (emojiMatcher.find()) {
            ""
        } else null
    }
}


