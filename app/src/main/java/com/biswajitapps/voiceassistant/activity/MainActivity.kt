package com.biswajitapps.voiceassistant.activity

import ai.api.android.AIConfiguration
import ai.api.android.GsonFactory
import ai.api.model.AIError
import ai.api.model.AIResponse
import ai.api.ui.AIDialog
import android.Manifest
import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.biswajitapps.voiceassistant.R
import com.biswajitapps.voiceassistant.adapter.MessageListAdapter
import com.biswajitapps.voiceassistant.ai.Config
import com.biswajitapps.voiceassistant.ai.TTS
import com.biswajitapps.voiceassistant.models.Message
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AIDialog.AIDialogListener, View.OnClickListener {

    private val REQ_CODE_SPEECH_INPUT = 1000
    private var adapter: MessageListAdapter? = null
    private var messages: ArrayList<Message>? = null
    private var aiDialog: AIDialog? = null
    private val REQUEST_AUDIO_PERMISSIONS_ID = 33

    private val TAG = MainActivity::class.java.name

    private val gson = GsonFactory.getGson()

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

        val config = AIConfiguration(Config.ACCESS_TOKEN,
                ai.api.AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System)

        aiDialog = AIDialog(this, config)
        aiDialog!!.setResultsListener(this)

        if (!checkAudioRecordPermission()) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_AUDIO_PERMISSIONS_ID)
        }
    }

    override fun onClick(v: View?) {
        promptSpeechInput()
    }

    /**
     * Showing google speech input dialog
     */
    private fun promptSpeechInput() {
        aiDialog!!.showAndListen()
        /*val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
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
        }*/
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

                    if (!result.isEmpty()) {
                        // If first Match contains the 'search' word
                        // Then start web search.
                        if (result[0].contains("search")) {

                            var searchQuery = result[0]
                            searchQuery = searchQuery.replace("search", "")
                            val search = Intent(Intent.ACTION_WEB_SEARCH)
                            search.putExtra(SearchManager.QUERY, searchQuery)
                            startActivity(search)
                        } else {
                            // populate the Matches
                            Toast.makeText(this@MainActivity, "Please say search ${result[0]} to search anything you want.", Toast.LENGTH_LONG).show()
                        }
                        //txtSpeechInput.setText(result[0])
                    }
                }
            }
        }
    }

    override fun onCancelled() {

    }

    override fun onResult(response: AIResponse?) {
        runOnUiThread {
            Log.d(TAG, "onResult")

            addNewMessage(response!!)

            Log.i(TAG, "Received success response")

            // this is example how to get different parts of result object
            val status = response.status
            Log.i(TAG, "Status code: " + status.code!!)
            Log.i(TAG, "Status type: " + status.errorType)

            val result = response.result
            Log.i(TAG, "Resolved query: " + result.resolvedQuery)

            Log.i(TAG, "Action: " + result.action)
            val speech = result.fulfillment.speech
            Log.i(TAG, "Speech: " + speech)
            TTS.init(this, speech)

            val metadata = result.metadata
            if (metadata != null) {
                Log.i(TAG, "Intent id: " + metadata.intentId)
                Log.i(TAG, "Intent name: " + metadata.intentName)
            }

            val params = result.parameters
            if (params != null && !params.isEmpty()) {
                Log.i(TAG, "Parameters: ")
                for ((key, value) in params) {
                    Log.i(TAG, String.format("%s: %s", key, value.toString()))
                }
            }
        }
    }

    private fun addNewMessage(response: AIResponse) {
        val sentMessage = Message()
        sentMessage.message = response.result.resolvedQuery
        sentMessage.sentFlag = true
        messages!!.add(sentMessage)

        val receivedMessage = Message()
        receivedMessage.message = response.result.fulfillment.speech
        receivedMessage.sentFlag = true
        messages!!.add(receivedMessage)

        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onError(error: AIError?) {
        runOnUiThread {
            Toast.makeText(this@MainActivity, error.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        if (aiDialog != null) {
            aiDialog!!.pause()
        }
        super.onPause()
    }

    override fun onResume() {
        if (aiDialog != null) {
            aiDialog!!.resume()
        }
        super.onResume()
    }

    private fun checkAudioRecordPermission() =
            ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_AUDIO_PERMISSIONS_ID -> {
                // If request is cancelled, the result arrays are empty.
                if (!checkAudioRecordPermission()) {
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.RECORD_AUDIO),
                            REQUEST_AUDIO_PERMISSIONS_ID)
                }
                return
            }
        }
    }

}
//https://blog.sendbird.com/android-chat-tutorial-building-a-messaging-ui/  refer chat ui