package com.example.rss.ui.home

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.R
import com.example.rss.model.RSSItem
import com.example.rss.rss.RecyclerAdapter
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.*
import java.lang.ref.WeakReference
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class HomeFragment : Fragment() {

    var adapter: RecyclerAdapter? = null
    var itemsList = ArrayList<RSSItem>()
    var recyclerView : RecyclerView?= null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.rss_list, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = RecyclerAdapter(itemsList, activity)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        recyclerView?.adapter = adapter

        val downloadData = FetchRSS(this)
        downloadData.execute("https://www.benchmark.pl/rss/benchmark-pl.xml")
    }

    fun updateRV(rssList: List<RSSItem>) {
        if (!rssList.isNullOrEmpty()) {
            itemsList.addAll(rssList)
            adapter?.notifyDataSetChanged()
        }
    }

    // fetch rss
    companion object {
        private class FetchRSS(context: HomeFragment) : AsyncTask<String, Void, List<RSSItem>>() {
            val weakReference = WeakReference(context)
            private val rssItemList = ArrayList<RSSItem>()
            private var rssItem : RSSItem ?= null

            override fun doInBackground(vararg url: String?): List<RSSItem>? {
                var rssItems: List<RSSItem>? = null
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e("RSS", "RSS Feed Empty")
                } else {
                    try {
                        val inputStream = ByteArrayInputStream(rssFeed.toByteArray(Charsets.UTF_8))
                        rssItems = parse(inputStream)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                return rssItems
            }

            override fun onPostExecute(result: List<RSSItem>?) {
                super.onPostExecute(result)
                if (!result.isNullOrEmpty()) {
                    weakReference.get()?.updateRV(result)
                }
            }

            private fun downloadXML(urlPath: String?): String {
                return URL(urlPath).readText()
            }

            fun parse(inputStream: InputStream): List<RSSItem> {
                val builderFactory = DocumentBuilderFactory.newInstance()
                val docBuilder = builderFactory.newDocumentBuilder()
                val doc = docBuilder.parse(inputStream)
                val nodeList = doc.getElementsByTagName("item")
                for (i in 0 until nodeList.length) {
                    if (nodeList.item(0).nodeType == Node.ELEMENT_NODE) {
                        rssItem?.let { rssItemList.add(it) }
                        val nodeElement = nodeList.item(i) as Element
                        rssItem = RSSItem()
                        // title
                        var text = getNodeValue("title", nodeElement)
                        rssItem!!.title = text
                        // link
                        text = getNodeValue("link", nodeElement)
                        rssItem!!.link = text
                        // description node
                        text = getNodeValue("description", nodeElement)
                        //img link
                        val doc: Document = Jsoup.parse(text)
                        var srcImg = doc.select("img").attr("src")
                        var imageLink = "https:$srcImg"
                        rssItem!!.imgLink = imageLink
                        //description
                        var strToCut = doc.select("img").toString()
                        var desc = doc.toString().split(strToCut)
                        var tmp = desc[1].dropLast(17)
                        rssItem!!.description = tmp
                    }
                }
                return rssItemList
            }

            fun getNodeValue(tag: String, nodeElement: Element): String {
                val nodeList = nodeElement.getElementsByTagName(tag)
                val node = nodeList.item(0)
                if (node != null) {
                    return node.textContent
                }
                return ""
            }
        }
    }


}


