package com.example.ecocrafters.utils

import android.util.Patterns
import android.widget.ImageView
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory

@GlideModule
class GlideApplication : AppGlideModule()

internal fun ImageView.loadRoundImage(url: String?) {
    if (url.isInvalidUrl()){
        return
    }
    val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

    GlideApp.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.NONE )
        .skipMemoryCache(true)
//        .thumbnail(Glide.with(context).load(R.drawable.ic_progress_bar))
        .apply(RequestOptions.bitmapTransform(CircleCrop()))
        .transition(DrawableTransitionOptions.withCrossFade(factory))
        .into(this)
}

internal fun ImageView.loadRectImage(url: String?) {
    if (url.isInvalidUrl()){
        return
    }
    val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

    GlideApp.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.NONE )
        .skipMemoryCache(true)
//        .thumbnail(Glide.with(context).load(R.drawable.ic_progress_bar))
//        .apply(RequestOptions.bitmapTransform(CircleCrop()).override(128))
        .transition(DrawableTransitionOptions.withCrossFade(factory))
        .into(this)
}

fun String?.isInvalidUrl(): Boolean {
    return if (this == null) {
        true
    } else {
        Patterns.WEB_URL.matcher(this).matches().not()
    }
}