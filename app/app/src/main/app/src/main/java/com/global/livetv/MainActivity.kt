package com.global.livetv

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

data class Channel(
    val id: String,
    val name: String,
    val country: String,
    val category: String,
    val languages: String,
    val logo: String
)

class MainActivity : AppCompatActivity() {

    // 🔴 আপনার গুগল অ্যাপস স্ক্রিপ্টের Web App লিঙ্ক এখানে দিন
    private val SCRIPT_URL = "https://script.google.com/macros/s/AKfycbxd-TR4dWGjxEENEG900Zzi2wttqfZMi1yseRn4-wvFRdpTr6IHVvTGgJxUmekxmTCvNQ/exec"

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var recyclerView: RecyclerView
    private val httpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ডায়নামিক ফুল-স্ক্রিন লেআউট
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setBackgroundColor(android.graphics.Color.parseColor("#101010"))
        }

        // গুগলের এক্সোপ্লেয়ার ভিউ
        playerView = PlayerView(this).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 650
            )
            setBackgroundColor(android.graphics.Color.BLACK)
        }

        recyclerView = RecyclerView(this).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT
            )
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        layout.addView(playerView)
        layout.addView(recyclerView)
        setContentView(layout)

        // গুগল এক্সোপ্লেয়ার তৈরি
        exoPlayer = ExoPlayer.Builder(this).build()
        playerView.player = exoPlayer

        loadChannelsFromDatabase()
    }

    private fun loadChannelsFromDatabase() {
        val request = Request.Builder().url(SCRIPT_URL).build()
        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "সার্ভার লোড হতে পারছে না!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string() ?: return
                val type = object : TypeToken<List<Channel>>() {}.type
                val channelList: List<Channel> = Gson().fromJson(json, type)

                runOnUiThread {
                    recyclerView.adapter = ChannelAdapter(channelList) { channel ->
                        playSecureChannel(channel.id)
                    }
                }
            }
        })
    }

    // ব্যাকএন্ড গেটওয়ে থেকে নিরাপদ স্ট্রিম ফেচ
    private fun playSecureChannel(channelId: String) {
        val securePlayUrl = "$SCRIPT_URL?action=play&id=$channelId"
        val request = Request.Builder().url(securePlayUrl).build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string() ?: return
                try {
                    val json = JSONObject(res)
                    if (json.optString("status") == "success") {
                        val streamUri = json.getString("stream_url")
                        runOnUiThread {
                            val mediaItem = MediaItem.fromUri(Uri.parse(streamUri))
                            exoPlayer.setMediaItem(mediaItem)
                            exoPlayer.prepare()
                            exoPlayer.play()
                        }
                    }
                } catch (e: Exception) {}
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

    // অ্যাডাপ্টার
    inner class ChannelAdapter(
        private val list: List<Channel>,
        private val onSelect: (Channel) -> Unit
    ) : RecyclerView.Adapter<ChannelAdapter.CardHolder>() {

        inner class CardHolder(val card: android.widget.LinearLayout, val title: TextView, val sub: TextView, val img: ImageView) 
            : RecyclerView.ViewHolder(card)

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): CardHolder {
            val card = android.widget.LinearLayout(parent.context).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                setPadding(24, 20, 24, 20)
                gravity = android.view.Gravity.CENTER_VERTICAL
                setBackgroundColor(android.graphics.Color.parseColor("#1C1C1C"))
                val params = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 8) }
                layoutParams = params
            }

            val img = ImageView(parent.context).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(120, 120)
            }

            val textLayout = android.widget.LinearLayout(parent.context).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(24, 0, 0, 0)
            }

            val title = TextView(parent.context).apply {
                textSize = 16f
                setTextColor(android.graphics.Color.WHITE)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val sub = TextView(parent.context).apply {
                textSize = 12f
                setTextColor(android.graphics.Color.LTGRAY)
            }

            textLayout.addView(title)
            textLayout.addView(sub)
            card.addView(img)
            card.addView(textLayout)

            return CardHolder(card, title, sub, img)
        }

        override fun onBindViewHolder(holder: CardHolder, position: Int) {
            val ch = list[position]
            holder.title.text = ch.name
            holder.sub.text = "${ch.country} • ${ch.category}"

            Glide.with(holder.itemView)
                .load(ch.logo)
                .placeholder(android.R.drawable.ic_menu_slideshow)
                .into(holder.img)

            holder.card.setOnClickListener { onSelect(ch) }
        }

        override fun getItemCount() = list.size
    }
}
