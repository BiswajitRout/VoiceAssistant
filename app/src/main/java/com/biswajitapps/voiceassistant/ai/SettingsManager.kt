/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.biswajitapps.voiceassistant.ai

import android.content.Context
import android.content.SharedPreferences

import com.biswajitapps.voiceassistant.application.VAApplication

class SettingsManager(private val context: Context) {
    private val prefs: SharedPreferences

    private var useBluetooth: Boolean = false

    init {
        prefs = context.getSharedPreferences(SETTINGS_PREFS_NAME, Context.MODE_PRIVATE)

        useBluetooth = prefs.getBoolean(PREF_USE_BLUETOOTH, true)
    }

    var isUseBluetooth: Boolean
        get() = useBluetooth
        set(useBluetooth) {
            this.useBluetooth = useBluetooth

            prefs.edit().putBoolean(PREF_USE_BLUETOOTH, useBluetooth).commit()
            val controller = (context.applicationContext as VAApplication).getBluetoothController()
            if (useBluetooth) {
                controller!!.start()
            } else {
                controller!!.stop()
            }
        }

    companion object {

        private val SETTINGS_PREFS_NAME = "ai.api.APP_SETTINGS"
        private val PREF_USE_BLUETOOTH = "USE_BLUETOOTH"
    }

}
