package com.braczkow.lorempicsum.ux

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.braczkow.lorempicsum.R
import com.braczkow.lorempicsum.app.App
import com.braczkow.lorempicsum.lib.PicsumApi
import com.braczkow.lorempicsum.lib.util.SchedulersFactory
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
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

        val adapter = ImagesAdapter()

        main_recycler.adapter = adapter
        main_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        main_test_btn.setOnClickListener {
            picsumApi.getPicsList()
                .subscribeOn(sf.io())
                .observeOn(sf.main())
                .subscribe({
                    Timber.d("Success geting picslist! size: ${it.size}")
                }, {
                    Timber.e("Failed to getPiclist: $it")
                })
        }
    }

    class ImagesAdapter: RecyclerView.Adapter<ImagesAdapter.ImageVH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVH {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getItemCount(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onBindViewHolder(holder: ImageVH, position: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        class ImageVH(itemView: View): RecyclerView.ViewHolder(itemView) {

        }
    }
}
