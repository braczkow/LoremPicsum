package com.braczkow.lorempicsum.ux.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.braczkow.lorempicsum.R
import com.braczkow.lorempicsum.app.App
import com.braczkow.lorempicsum.app.di.ViewModelKey
import com.braczkow.lorempicsum.ux.details.DetailsActivity
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import kotlinx.android.synthetic.main.activity_main.*
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

        val adapter = ImagesAdapter(makeNavigation())
        main_recycler.adapter = adapter

        val columnsNo = applicationContext.resources.getInteger(R.integer.default_cols_no)
        main_recycler.layoutManager =
            GridLayoutManager(this, columnsNo, GridLayoutManager.VERTICAL, false)

        main_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    vm.requestImages()
                }
            }
        })

        vm.picsumPicsum.observe(this, Observer {
            adapter.setItems(it)
        })

        vm.isLoading.observe(this, Observer {
            main_progress.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    private fun makeNavigation(): MainNavigation {
        return object : MainNavigation {
            override fun navigate(to: MainNavigation.Destination) {
                when (to) {
                    is MainNavigation.Destination.ImageDetails -> {
                        val intent = DetailsActivity.makeIntent(
                            this@MainActivity,
                            to.id,
                            to.downloadUrl,
                            to.author
                        )

                        val opts = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this@MainActivity,
                            to.view,
                            ViewCompat.getTransitionName(to.view)!!
                        )

                        startActivity(
                            intent,
                            opts.toBundle()
                        )
                    }
                }
            }

        }
    }


}
