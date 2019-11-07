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
import com.braczkow.lorempicsum.R
import com.braczkow.lorempicsum.app.App
import com.braczkow.lorempicsum.app.di.ViewModelKey
import com.braczkow.lorempicsum.lib.picsum.PicsumApi
import com.braczkow.lorempicsum.lib.picsum.PicsumRepository
import com.braczkow.lorempicsum.lib.util.SchedulersFactory
import com.braczkow.lorempicsum.ux.details.DetailsActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import io.reactivex.disposables.CompositeDisposable
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

    class AndroidViewModel @Inject constructor(
        private val picsumApi: PicsumApi,
        private val picsumRepository: PicsumRepository,
        private val sf: SchedulersFactory
    ): ViewModel() {

        val disposables = CompositeDisposable()
        val picsumList : LiveData<List<PicsumApi.ListEntry>> = MutableLiveData()
        val isLoading: LiveData<Boolean> = MutableLiveData()

        init {
            Timber.d("AndroidViewModel init")
            picsumRepository
                .getPiclist()
                .subscribe {
                    if (it.isEmpty()) {
                        fetchNewImages()
                    } else {
                        (picsumList as MutableLiveData).postValue(it)
                    }
                }.apply { disposables.add(this) }

        }

        fun fetchNewImages() {
            if (isLoading.value == true) {
                Timber.d("loading in progress, early return")
                return
            }

            setLoading(true)

            val requestPage = picsumRepository.getPagesFetched() + 1

            picsumApi.getPicsList(requestPage)
            .subscribeOn(sf.io())
            .observeOn(sf.main())
            .subscribe({
                Timber.d("Success geting picslist! size: ${it.size}")
                setLoading(false)
                picsumRepository.addImages(it, requestPage)
            }, {
                Timber.e("Failed to getPiclist: $it")
                setLoading(false)
            }).apply { disposables.add(this) }
        }

        private fun setLoading(loading: Boolean) {
            (isLoading as MutableLiveData).postValue(loading)
        }

        override fun onCleared() {
            super.onCleared()
            disposables.dispose()
        }

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
                    vm.fetchNewImages()
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

            Glide.with(context)
                .load(item.download_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
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
