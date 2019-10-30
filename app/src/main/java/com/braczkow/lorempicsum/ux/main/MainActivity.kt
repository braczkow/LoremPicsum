package com.braczkow.lorempicsum.ux.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.braczkow.lorempicsum.R
import com.braczkow.lorempicsum.app.App
import com.braczkow.lorempicsum.lib.picsum.PicsumApi
import com.braczkow.lorempicsum.lib.picsum.PicsumRepository
import com.braczkow.lorempicsum.lib.util.SchedulersFactory
import com.braczkow.lorempicsum.ux.details.DetailsActivity
import com.bumptech.glide.Glide
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.image_item.view.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Subcomponent(modules = [DaggerModule::class])
    interface DaggerComponent {
        @Subcomponent.Builder
        interface Builder {
            fun build(): DaggerComponent
            fun plus(module: DaggerModule): Builder
        }

        fun inject(mainActivity: MainActivity)
    }

    @Module
    class DaggerModule(
        val lifecycle: Lifecycle,
        val mainView: MainView
    ) {
        @Provides
        fun provideLifecycle() = lifecycle

        @Provides
        fun provideView() = mainView
    }


    class MainView(private val rootView: View) {
        fun refreshItems() {
            rootView.main_recycler.adapter?.notifyDataSetChanged()
        }
    }

    @Inject
    lateinit var mainPresenter: MainPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        App
            .dagger()
            .mainActivity()
            .plus(DaggerModule(lifecycle, MainView(main_root)))
            .build()
            .inject(this)

        val adapter = ImagesAdapter(this, mainPresenter)

        main_recycler.adapter = adapter
        main_recycler.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
    }



    class ImagesAdapter(private val context: Context, private val presenter: MainPresenter) :
        RecyclerView.Adapter<ImagesAdapter.ImageVH>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVH {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
            return ImageVH(view)
        }

        override fun getItemCount() = presenter.items.size

        override fun onBindViewHolder(holder: ImageVH, position: Int) {
            val item = presenter.items[position]

            Glide.with(context)
                .load(item.download_url)
                .into(holder.itemView.image_image)

            holder.itemView.image_root.setOnClickListener {
                context.startActivity(
                    DetailsActivity.makeIntent(
                        context,
                        item.download_url,
                        item.author
                    )
                )
            }
        }

        class ImageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        }
    }
}
