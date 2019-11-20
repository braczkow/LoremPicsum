package com.braczkow.lorempicsum.ux.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.braczkow.lorempicsum.R
import com.braczkow.lorempicsum.lib.picsum.PicsumEntry
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.image_item.view.*
import timber.log.Timber

class ImagesAdapter(private val navigation: MainNavigation) :
    RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    internal var items = listOf<PicsumEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        Timber.d("getItemCount: ${items.size}")
        return items.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) = bindItem(holder, items[position])

    internal fun bindItem(view: ImageView, item: PicsumEntry) {
        view.setTransitionName(item.id)

        view.loadImage(item.download_url)

        view.imageClicks {
            navigation.navigate(MainNavigation.Destination.ImageDetails(item.id, item.download_url, item.author, view.getImageView()))
        }
    }

    fun setItems(it: List<PicsumEntry>) {
        items = it
        notifyDataSetChanged()
    }

    interface ImageView {
        fun loadImage(url: String)
        fun imageClicks(clicks: () -> Unit)
        fun setTransitionName(id: String)
        fun getImageView(): View
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ImageView {
        override fun setTransitionName(id: String) {
            ViewCompat.setTransitionName(itemView.image_image, id)
        }

        override fun loadImage(url: String) {
            val context = itemView.context
            val progressDrawable = CircularProgressDrawable(
                context
            ).apply {
                centerRadius = 30f
                start()
            }

            Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(progressDrawable)
                .into(itemView.image_image)
        }

        override fun imageClicks(clicks: () -> Unit) {
            itemView.image_root.setOnClickListener {
                clicks()
            }
        }

        override fun getImageView() = itemView.image_image
    }
}