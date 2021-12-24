package com.example.animeshark

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

data class QueryResult(val title: String, val url: String, val img: String)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        searchAMP("Mushoku")
//      getEpisodes()
    }

    /** Get Animixplay episodes **/
    fun getEpisodes() {
        val stringRequest = StringRequest( Request.Method.GET, "https://animixplay.to/v1/mushoku-tensei-isekai-ittara-honki-dasu",
             { response ->
                 val eplist = JSONObject(Jsoup.parse(response).select("div#epslistplace").text())
                 Log.i("MY_DEBUG", eplist["0"].toString())
             },
            { error ->
                Log.i("MY_DEBUG", "Response is: $error")
            })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    /** Search Animixplay **/
    fun searchAMP(query: String) {
        val stringRequest: StringRequest = object : StringRequest( Method.POST, "https://cachecow.eu/api/search",
            Response.Listener { response ->
                val soup = Jsoup.parse(JSONObject(response)["result"].toString())
                val items = mutableListOf<QueryResult>()
                for (li in soup.select("li")) {
                    val alink = li.select("a")[0]
                    val title = alink.attr("title").toString()
                    val url = "https://animixplay.to" + alink.attr("href").toString()
                    val img = alink.select("img").attr("src").toString()
                    items.add(QueryResult(title, url, img))
                }
                val recyclerView: RecyclerView = findViewById(R.id.search_results)
                recyclerView.adapter = AnimeQueryItemAdapter(items)
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

    /** Called when the user taps the Send button */
    fun sendMessage(view: View) {
        val editText = findViewById<EditText>(R.id.editTextTextPersonName)
        val message = editText.text.toString()
        Log.i("MY_DEBUG",message)
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        searchAMP(message)
//        startActivity(intent)
    }
}