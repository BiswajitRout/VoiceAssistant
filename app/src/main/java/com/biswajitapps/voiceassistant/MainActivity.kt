package com.biswajitapps.voiceassistant

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.biswajitapps.voiceassistant.adapter.MessageListAdapter
import com.biswajitapps.voiceassistant.models.Message
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val REQ_CODE_SPEECH_INPUT = 1000
    private var adapter: MessageListAdapter? = null
    private var messages: ArrayList<Message>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messages = ArrayList()
        adapter = MessageListAdapter(messages!!)
        val layoutManager = LinearLayoutManager(this)
        /*layoutManager.reverseLayout = true*/
        layoutManager.stackFromEnd = true
        rvMessageList.layoutManager = layoutManager
        rvMessageList.adapter = adapter

        fabMic.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        promptSpeechInput()
    }

    /**
     * Showing google speech input dialog
     */
    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt))
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(applicationContext,
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Receiving speech input
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    val result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val message = Message()
                    message.message = result[0]
                    messages!!.add(message)

                    if (adapter != null) {
                        adapter!!.notifyDataSetChanged()
                    }

                    /*if (!textMatchList.isEmpty()) {
     // If first Match contains the 'search' word
     // Then start web search.
     if (textMatchList.get(0).contains("search")) {

        String searchQuery = textMatchList.get(0);
                                           searchQuery = searchQuery.replace("search","");
        Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
        search.putExtra(SearchManager.QUERY, searchQuery);
        startActivity(search);
     } else {
         // populate the Matches
         mlvTextMatches
      .setAdapter(new ArrayAdapter<string>(this,
        android.R.layout.simple_list_item_1,
        textMatchList));
     }*/
                    //Toast.makeText(this@MainActivity, result[0], Toast.LENGTH_LONG).show()
                    //txtSpeechInput.setText(result[0])
                }
            }
        }
    }
}

//https://blog.sendbird.com/android-chat-tutorial-building-a-messaging-ui/  refer chat ui