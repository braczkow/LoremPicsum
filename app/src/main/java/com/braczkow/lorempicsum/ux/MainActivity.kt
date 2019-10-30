package com.braczkow.lorempicsum.ux

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.braczkow.lorempicsum.R
import com.braczkow.lorempicsum.app.App
import com.braczkow.lorempicsum.lib.PicsumApi
import com.braczkow.lorempicsum.lib.util.SchedulersFactory
import com.braczkow.lorempicsum.ux.details.DetailsActivity
import com.bumptech.glide.Glide
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.image_item.view.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var picsumApi: PicsumApi

    @Inject
    lateinit var sf: SchedulersFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        App
            .dagger()
            .inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = ImagesAdapter(this)

        main_recycler.adapter = adapter
        main_recycler.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)


        main_test_btn.setOnClickListener {
            picsumApi.getPicsList()
                .subscribeOn(sf.io())
                .observeOn(sf.main())
                .subscribe({
                    Timber.d("Success geting picslist! size: ${it.size}")
                    adapter.addItems(it)
                }, {
                    Timber.e("Failed to getPiclist: $it")
                })
        }
    }

    class ImagesAdapter(private val context: Context): RecyclerView.Adapter<ImagesAdapter.ImageVH>() {
        private val items = mutableListOf<PicsumApi.ListEntry>()

        fun addItems(items: List<PicsumApi.ListEntry>) {
            this.items.addAll(items)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
            return ImageVH(view)
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: ImageVH, position: Int) {
            val item = items[position]

            Glide.with(context)
                .load(item.download_url)
                .into(holder.itemView.image_image)

            holder.itemView.image_root.setOnClickListener {
                context.startActivity(DetailsActivity.makeIntent(context, item.download_url, item.author))
            }
        }

        class ImageVH(itemView: View): RecyclerView.ViewHolder(itemView) {

        }
    }
}
