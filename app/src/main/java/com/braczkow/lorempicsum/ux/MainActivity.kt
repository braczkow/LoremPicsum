package com.braczkow.lorempicsum.ux

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.braczkow.lorempicsum.R
import com.braczkow.lorempicsum.app.App
import com.braczkow.lorempicsum.lib.PicsumApi
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var picsumApi: PicsumApi

    override fun onCreate(savedInstanceState: Bundle?) {
        App
            .dagger()
            .inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_test_btn.setOnClickListener {
            picsumApi.getPicsList()
                .subscribe({
                    Timber.d("Success geting picslist! size: ${it.entries.size}")
                }, {
                    Timber.e("Failed to getPiclist: $it")
                })
        }
    }
}
