package com.example.animeshark

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.jsoup.Jsoup
import android.app.DownloadManager
import android.os.Environment
import android.content.Context
import android.net.Uri
import android.view.View
import com.android.volley.NetworkResponse
import com.android.volley.toolbox.HurlStack
import com.android.volley.RequestQueue
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

fun isNumber(s: String): Boolean {
    return try {
        s.toInt()
        true
    } catch (ex: NumberFormatException) {
        false
    }
}

class AnimeInfoActivity : AppCompatActivity() {

    var data: MutableList<Episode> = mutableListOf<Episode>()
    var adapter: EpisodesAdapter? = null
    var animeName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_info)

        // Get the Intent that started this activity and extract the QueryResult
        val message = intent.getSerializableExtra(ANIME_INFO) as? QueryResult

        // Capture the layout's TextView and set the string as its text
        findViewById<TextView>(R.id.anime_title).apply {
            text = message?.title.toString()
        }

        val recyclerView: RecyclerView = findViewById(R.id.anime_episodes)
        adapter = EpisodesAdapter(data)  { episode -> adapterOnClick(episode)}
        recyclerView.adapter = adapter

        message?.let {
            getEpisodes(message.url)
            val reSpecial = Regex("[^A-Za-z0-9 ]")
            animeName = reSpecial.replace(message.title, "")
        }
    }

    private fun adapterOnClick(episode: Episode) {
        val idRegex = Regex("\\?id=(.*)&")
        val id = idRegex.find(episode.url)
        Log.i("MY_DEBUG", episode.url)
        if (id != null) {
            val downloadPage = "https://gogoplay1.com/download?id=" + id.groupValues[1]
            Log.i("MY_DEBUG", downloadPage)
            getDownloadLinks(downloadPage, episode.number)
        }
    }

    /** Get Animixplay episodes **/
    @SuppressLint("NotifyDataSetChanged")
    fun getEpisodes(url: String) {
        Log.i("MY_DEBUG", url)
        val stringRequest = StringRequest( Request.Method.GET, url,
            { response ->
                data.clear()
                val eplist = JSONObject(Jsoup.parse(response).select("div#epslistplace").text())
                for (i in eplist.keys()) {
                    if (isNumber(i)) {
                        val i2 = (i.toInt() + 1).toString()
                        Log.i("MY_DEBUG", i.toString())
                        data.add(Episode("Episode $i2", eplist[i].toString()))
                    }
                }
                Log.i("MY_DEBUG", data.toString())
                adapter?.notifyDataSetChanged();
            },
            { error ->
                Log.i("MY_DEBUG", "Response is: $error")
            })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun getDownloadLinks(url: String, episodeString: String) {
        val stringRequest = StringRequest( Request.Method.GET, url,
            { response ->
                val links = Jsoup.parse(response).select("div.dowload").select("a")
                val linkList = mutableListOf<String>()
                for (link in links) {
                    val url = link.attr("href")
                    if ("cdn" in url) {
                        Log.i("MY_DEBUG", link.text())
                        Log.i("MY_DEBUG", url)
                        linkList.add(url)
                    }
                }
                if (linkList.size > 0) {
                    downloadGogoCDN(linkList.last(), episodeString)
                }
            },
            { error ->
                Log.i("MY_DEBUG", "Response is: $error")
            })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun downloadGogoCDN(url: String, episodeString: String = "episodeString") {
        Log.i("MY_DEBUG_HERE", url)

        val uri = Uri.parse(url)
        Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .mkdirs();
        val mgr = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(uri)
            .addRequestHeader("Referer", "https://gogoplay1.com/")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("$animeName - $episodeString")
            .setDescription("Animeshark")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "$animeName/$episodeString - $animeName.mp4"
            )
        mgr.enqueue(request)
    }

}