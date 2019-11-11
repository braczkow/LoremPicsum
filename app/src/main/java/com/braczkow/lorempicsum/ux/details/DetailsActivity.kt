package com.braczkow.lorempicsum.ux.details

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.braczkow.lorempicsum.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.image_item.view.*

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        supportPostponeEnterTransition()

        var url: String? = null

        intent.extras?.let { bundle ->
            bundle.getString(URL_ARG_EXTRA)?.let {
                Glide.with(this)
                    .load(it)
                    .listener(object: RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            supportStartPostponedEnterTransition()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            supportStartPostponedEnterTransition()
                            return false
                        }

                    })
                    .into(details_image)


                url = it
            }

            bundle.getString(AUTHOR_ARG_EXTRA)?.let {
                details_author.setText(it)
            }

            bundle.getString(ANIMATION_NAME)?.let {
                details_image.transitionName = it
            }
        }

        details_share.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, url)
                type = "image/*"
            }

            startActivity(sendIntent)
        }
    }

    companion object {
        fun makeIntent(
            context: Context,
            id: String,
            url: String,
            author: String
        ) =
            Intent(context, DetailsActivity::class.java)
                .apply {
                    putExtra(URL_ARG_EXTRA, url)
                    putExtra(AUTHOR_ARG_EXTRA, author)
                    putExtra(ANIMATION_NAME, id)
                }

        private val URL_ARG_EXTRA = "URL_ARG_EXTRA"
        private val AUTHOR_ARG_EXTRA = "AUTHOR_ARG_EXTRA"
        private val ANIMATION_NAME = "ANIMATION_NAME"
    }
}
