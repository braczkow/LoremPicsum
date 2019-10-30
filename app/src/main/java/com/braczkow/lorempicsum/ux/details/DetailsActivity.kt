package com.braczkow.lorempicsum.ux.details

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.braczkow.lorempicsum.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.image_item.view.*

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        intent.extras?.let { bundle ->
            bundle.getString(URL_ARG_EXTRA)?.let {
                Glide.with(this)
                    .load(it)
                    .into(details_image)
            }

            bundle.getString(AUTHOR_ARG_EXTRA)?.let {
                details_author.setText(it)
            }
        }
    }

    companion object {
        fun makeIntent(context: Context, url: String, author: String) =
            Intent(context, DetailsActivity::class.java)
                .apply {
                    putExtra(URL_ARG_EXTRA, url)
                    putExtra(AUTHOR_ARG_EXTRA, author)
                }

        private val URL_ARG_EXTRA = "URL_ARG_EXTRA"
        private val AUTHOR_ARG_EXTRA = "AUTHOR_ARG_EXTRA"
    }
}
