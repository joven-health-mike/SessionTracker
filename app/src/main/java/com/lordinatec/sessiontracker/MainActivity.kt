package com.lordinatec.sessiontracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.lordinatec.sessiontracker.session.WifiEvent
import com.lordinatec.sessiontracker.session.WifiSession
import com.lordinatec.sessiontracker.ui.theme.SessionTrackerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val wifiEventProvider = MutableSharedFlow<WifiEvent>()

        val wifiSession = WifiSession("wifi_session", wifiEventProvider)
        enableEdgeToEdge()
        setContent {
            SessionTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Modifier.padding(innerPadding)
                    var refresh by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        lifecycleScope.launch {
                            wifiSession.listenForWifiEvents()
                        }
                        lifecycleScope.launch {
                            delay(1000)
                            refresh = !refresh
                            wifiEventProvider.emit(
                                WifiEvent.WifiConnected(
                                    "ssid",
                                    "bssid",
                                    0,
                                    0,
                                    0
                                )
                            )

                            delay(1000)
                            refresh = !refresh
                            wifiEventProvider.emit(WifiEvent.WifiSignalStrengthChanged(1234))
                            delay(1000)
                            refresh = !refresh
                            wifiEventProvider.emit(WifiEvent.WifiLinkSpeedChanged(5372))
                            delay(1000)
                            refresh = !refresh
                            wifiEventProvider.emit(WifiEvent.WifiFrequencyChanged(3575))
                            delay(1000)
                            refresh = !refresh
                            wifiEventProvider.emit(WifiEvent.WifiIpChanged("ip"))
                            delay(1000)
                            refresh = !refresh
                            wifiEventProvider.emit(WifiEvent.WifiMacChanged("mac"))
                            delay(1000)
                            refresh = !refresh
                            wifiEventProvider.emit(WifiEvent.WifiDisconnected("ssid", "bssid"))
                        }
                    }
                    LaunchedEffect(refresh) {
                        println("|---------------------------------|")
                        println("Session ID: ${wifiSession.sessionId}")
                        println("Session Active: ${wifiSession.isActive}")
                        println("Session Start Time: ${wifiSession.startTime}")
                        println("Session End Time: ${wifiSession.endTime}")
                        println("Session Duration: ${wifiSession.getSessionDuration()}")

                        wifiSession.getSessionData().forEach {
                            println("Session Data: ${it.key} -> ${it.value}")
                        }
                    }
                }
            }
        }
    }
}
