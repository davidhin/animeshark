package com.example.animeshark

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.jsoup.Jsoup

const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"
const val ANIME_INFO = "com.example.myfirstapp.ANIME_INFO"

class MainActivity : AppCompatActivity() {

    var data: MutableList<QueryResult> = mutableListOf<QueryResult>()
    var adapter: AnimeQueryItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.search_results)
        adapter = AnimeQueryItemAdapter(data)  { anime -> adapterOnClick(anime) }
        recyclerView.adapter = adapter
        searchAMP("hunter")
    }

    /** Search Animixplay **/
    fun searchAMP(query: String) {
        val stringRequest: StringRequest = @SuppressLint("NotifyDataSetChanged")
        object : StringRequest( Method.POST, "https://cachecow.eu/api/search",
            Response.Listener { response ->
                data.clear()
                val soup = Jsoup.parse(JSONObject(response)["result"].toString())
                for (li in soup.select("li")) {
                    val alink = li.select("a")[0]
                    val title = alink.attr("title").toString()
                    val url = "https://animixplay.to" + alink.attr("href").toString()
                    val img = alink.select("img").attr("src").toString()
                    data.add(QueryResult(title, url, img))
                }
                adapter?.notifyDataSetChanged();
            },
            Response.ErrorListener { error ->
                Log.i("MY_DEBUG", "Response is: $error")
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["qfast"] = query
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    /** Opens AnimeViewActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(anime: QueryResult) {
        val intent = Intent(this, AnimeInfoActivity()::class.java).apply {
            putExtra(ANIME_INFO, anime)
        }
        startActivity(intent)
    }

    /** Called when the user taps the Send button */
    fun sendSearch(view: View) {
        val editText = findViewById<EditText>(R.id.editTextTextPersonName)
        val message = editText.text.toString()
        searchAMP(message)
    }
}