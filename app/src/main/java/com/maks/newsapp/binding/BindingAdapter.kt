package com.maks.newsapp.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import maks.newsapp.R
import javax.inject.Inject

class BindingAdapter @Inject constructor() {
    companion object {
        @BindingAdapter("urlToImage")
        @JvmStatic
        fun loadImage(view: ImageView, urlToImage: String?) {
            try {
                Glide.with(view.context).setDefaultRequestOptions(RequestOptions())
                    .load(urlToImage).placeholder(R.drawable.bg_image_loader)
                    .error(R.drawable.bg_image_loader)
                    .into(view)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}