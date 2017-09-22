package com.biswajitapps.voiceassistant.application

import ai.api.util.BluetoothController
import android.app.Application
import android.content.Context
import android.util.Log
import com.biswajitapps.voiceassistant.ai.SettingsManager

/**
 * Created by Biswajit on 9/22/2017.
 * All rights reserved by BiswajitApps
 */
class VAApplication : Application() {

    private val TAG = VAApplication::class.java.getSimpleName()

    private var activitiesCount: Int = 0
    private var bluetoothController: BluetoothControllerImpl? = null
    private var settingsManager: SettingsManager? = null

    override fun onCreate() {
        super.onCreate()
        bluetoothController = BluetoothControllerImpl(this)
        settingsManager = SettingsManager(this)
    }

    fun getBluetoothController(): BluetoothController? {
        return bluetoothController
    }

    fun getSettingsManager(): SettingsManager? {
        return settingsManager
    }

    protected fun onActivityResume() {
        if (activitiesCount++ == 0) { // on become foreground
            if (settingsManager!!.isUseBluetooth) {
                bluetoothController!!.start()
            }
        }
    }

    protected fun onActivityPaused() {
        if (--activitiesCount == 0) { // on become background
            bluetoothController!!.stop()
        }
    }

    private fun isInForeground(): Boolean {
        return activitiesCount > 0
    }

    private inner class BluetoothControllerImpl(context: Context) : BluetoothController(context) {

        override fun onHeadsetDisconnected() {
            Log.d(TAG, "Bluetooth headset disconnected")
        }

        override fun onHeadsetConnected() {
            Log.d(TAG, "Bluetooth headset connected")

            if (isInForeground() && settingsManager!!.isUseBluetooth
                    && !bluetoothController!!.isOnHeadsetSco) {
                bluetoothController!!.start()
            }
        }

        override fun onScoAudioDisconnected() {
            Log.d(TAG, "Bluetooth sco audio finished")
            bluetoothController!!.stop()

            if (isInForeground() && settingsManager!!.isUseBluetooth) {
                bluetoothController!!.start()
            }
        }

        override fun onScoAudioConnected() {
            Log.d(TAG, "Bluetooth sco audio started")
        }

    }


}