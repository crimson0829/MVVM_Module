package com.crimson.mvvm.binding

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.crimson.mvvm.binding.consumer.BindConsumer
import com.crimson.mvvm.ext.dp2px
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


/**
 * bind image with glide
 * imageUrl：图片链接
 * imageStyle：加载方式
 * imageRoundShape：设置图片圆角大小
 * image_skipMemoryCache：是否忽略内存缓存
 * image_diskMemoryCache：本地缓存策略
 * imagePlaceholder：欲加载显示图片
 * imageError：加载错误显示图片
 */
@BindingAdapter(
    "app:imageUrl",
    "app:imageStyle",
    "app:imageRoundShape",
    "app:image_skipMemoryCache",
    "app:image_diskMemoryCache",
    "app:imagePlaceholder",
    "app:imageError",
    "app:imageLoadSuc",
    "app:imageLoadFail",
    requireAll = false
)
fun ImageView.bindImage(
    imageUrl: String?,
    imageStyle: Int? = 1,
    imageRoundShape: Int? = 0,
    skipMemoryCache: Boolean = false,
    diskMemoryCache: Int? = 1,
    @DrawableRes imagePlaceholder: Int = 0,
    @DrawableRes imageError: Int = 0,
    loadSucConsumer: BindConsumer<Drawable>? = null,
    loadFalConsumer: BindConsumer<GlideException>? = null

) {

    val builder = Glide.with(context)
        .load(imageUrl)
        .skipMemoryCache(skipMemoryCache)
        .placeholder(imagePlaceholder)
        .centerCrop()
        .error(imageError)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                loadFalConsumer?.accept(e)
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                loadSucConsumer?.accept(resource)
                return false

            }
        })

    when (imageStyle) {
        //默认
        1 -> builder.centerCrop()
        //round shape，设置imageStyle=2 再设置imageRoundShape>0才有效果
        2 -> {
            if (imageRoundShape != 0) {
                builder.transform(RoundedCornersTransformation(dp2px(imageRoundShape ?: 0), 0))
            } else {
                builder.centerCrop()
            }
        }
        //circle
        3 -> builder.circleCrop()
        //blur 高斯模糊
        4 -> builder.transform(BlurTransformation(25, 5))
        5 -> builder.centerInside()
        6 -> builder.fitCenter()
        else -> builder.centerCrop()

    }

    when (diskMemoryCache) {

        1 -> builder.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        2 -> builder.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        3 -> builder.diskCacheStrategy(DiskCacheStrategy.DATA)
        4 -> builder.diskCacheStrategy(DiskCacheStrategy.NONE)
        5 -> builder.diskCacheStrategy(DiskCacheStrategy.ALL)
        else -> builder.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

    }

    builder.into(this)

}

@BindingAdapter("app:imageRes")
infix fun ImageView.set(@DrawableRes id: Int) {
    setImageResource(id)
}


@BindingAdapter("app:imageGif")
infix fun ImageView.bindGif(@DrawableRes id: Int) {
    Glide.with(this).load(id).into(this)
}

@BindingAdapter("app:imageBitmap")
infix fun ImageView.set(bitmap: Bitmap?) {
    bitmap?.let {
        setImageBitmap(it)
    }
}

@BindingAdapter("app:imageDrawable")
infix fun ImageView.set(drawable: Drawable?) {
    drawable?.let {
        setImageDrawable(it)
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@BindingAdapter("app:imageIcon")
infix fun ImageView.set(ic: Icon) {
    setImageIcon(ic)
}

@BindingAdapter("app:imageUri")
infix fun ImageView.set(uri: Uri) {
    setImageURI(uri)
}

