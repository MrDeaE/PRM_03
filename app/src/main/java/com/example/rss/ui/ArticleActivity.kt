package com.example.rss.ui

import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rss.R
import com.example.rss.rss.GlideApp
import kotlinx.android.synthetic.main.activity_article.*
import org.jsoup.Jsoup

class ArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        layout_article.bringToFront()

        article_title.text = intent.extras?.get("title").toString()
        var link = intent.extras?.get("link").toString()
        var imgLink = intent.extras?.get("image")

        if(!link.isNullOrEmpty()) {
            this?.let {
                GlideApp.with(it)
                    .load(imgLink)
                    .into(image_article)
            }
        }

        image_article.setImageURI(Uri.parse(intent.extras?.get("image").toString()))

        button_back_from_article.setOnClickListener{ view ->
            finish()
        }

        webview.settings.domStorageEnabled
        doAsync{
            val document = Jsoup.connect(link).get()
            val content = document.getElementById("content").outerHtml()
            runOnUiThread{
                webview.loadData(content, "text/html", "utf-8")
            }
        }.execute()

    }

}

class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }
}