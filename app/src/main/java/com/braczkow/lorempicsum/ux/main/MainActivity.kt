package com.braczkow.lorempicsum.ux.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.braczkow.lorempicsum.R
import com.braczkow.lorempicsum.app.App
import com.braczkow.lorempicsum.app.di.ViewModelKey
import com.braczkow.lorempicsum.lib.picsum.PicsumApi
import com.braczkow.lorempicsum.ux.details.DetailsActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.image_item.view.*
import timber.log.Timber
import javax.inject.Inject



class MainActivity : AppCompatActivity() {

    @Module(includes = [DaggerModule.VmBinding::class])
    class DaggerModule {
        @Module
        abstract class VmBinding {
            @Binds
            @IntoMap
            @ViewModelKey(AndroidViewModel::class)
            abstract fun bind(vm: AndroidViewModel): ViewModel
        }
    }

    @Subcomponent(modules = [DaggerModule::class])
    interface DaggerComponent {
        @Subcomponent.Builder
        interface Builder {
            fun build(): DaggerComponent
            fun plus(module: DaggerModule): Builder
        }

        fun inject(mainActivity: MainActivity)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var vm: AndroidViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        App
            .dagger()
            .mainActivity()
            .plus(DaggerModule())
            .build()
            .inject(this)

        vm = ViewModelProviders.of(this, viewModelFactory).get(AndroidViewModel::class.java)

        val adapter = ImagesAdapter(this)
        main_recycler.adapter = adapter
        main_recycler.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        main_recycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    vm.requestImages()
                }
            }
        })

        vm.picsumList.observe(this, Observer {
            adapter.setItems(it)
        })

        vm.isLoading.observe(this, Observer {
            main_progress.visibility = if (it) View.VISIBLE else View.GONE
        })
    }



    class ImagesAdapter(private val context: Context) :
        RecyclerView.Adapter<ImagesAdapter.ImageVH>() {

        private var items = listOf<PicsumApi.ListEntry>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVH {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
            return ImageVH(view)
        }

        override fun getItemCount(): Int {
            Timber.d("getItemCount: ${items.size}")
            return items.size
        }

        override fun onBindViewHolder(holder: ImageVH, position: Int) {
            val item = items[position]

            val progressDrawable = CircularProgressDrawable(context).apply {
                centerRadius = 30f
                start()
            }

            Glide.with(context)
                .load(item.download_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(progressDrawable)
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

        fun setItems(it: List<PicsumApi.ListEntry>) {
            items = it
            notifyDataSetChanged()
        }

        class ImageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        }
    }
}
