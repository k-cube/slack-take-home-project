package com.slack.exercise.dataBinding

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions


/**
 * Sets the [ImageView] image using Glide with the provided [url].
 *
 * Example Usage:
 * ```
 * app:imageUrl="@{model.imageUrl}"
 * app:placeholder="@{@drawable/ic_no_image_srp_120}"
 * ```
 */
@BindingAdapter("imageUrl", "placeholder", requireAll = false)
fun ImageView.loadImage(url: String?, placeholder: Drawable?) {
    var requestOptions = RequestOptions()
    requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(8))
    Glide.with(this).load(url).apply(requestOptions).placeholder(placeholder).into(this)
}
