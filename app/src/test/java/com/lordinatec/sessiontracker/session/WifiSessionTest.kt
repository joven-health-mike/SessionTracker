package com.lordinatec.sessiontracker.session

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class WifiSessionTest {
    private val fakeProvider: MutableSharedFlow<WifiEvent> = MutableSharedFlow()

    private lateinit var wifiSession: WifiSession

    @BeforeTest
    fun setup() {
        wifiSession = WifiSession(
            sessionId = "1234", wifiEventProvider = fakeProvider
        )
    }

    @Test
    fun testCreateWifiSession() = runTest {
        val session = WifiSession(
            sessionId = "1234", wifiEventProvider = fakeProvider
        )
        assertNotNull(session)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testWifiSessionConnected() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            wifiSession.listenForWifiEvents()
        }
        fakeProvider.emit(
            WifiEvent.WifiConnected(
                ssid = "ssid", bssid = "bssid", rssi = 0, frequency = 0, linkSpeed = 0
            )
        )
        val expectedWifiData = wifiSession.getSessionData("ssid")
        assertNotNull(expectedWifiData)
        assertEquals("ssid", expectedWifiData.ssid)
        assertEquals("bssid", expectedWifiData.bssid)
        assertEquals(0, expectedWifiData.rssi)
        assertEquals(0, expectedWifiData.frequency)
        assertEquals(0, expectedWifiData.linkSpeed)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testWifiSessionConnectedThenDisconnected() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            wifiSession.listenForWifiEvents()
        }
        fakeProvider.emit(
            WifiEvent.WifiConnected(
                ssid = "ssid", bssid = "bssid", rssi = 0, frequency = 0, linkSpeed = 0
            )
        )
        // sleep so that the duration is greater than 0
        Thread.sleep(1L)
        fakeProvider.emit(
            WifiEvent.WifiDisconnected(
                ssid = "ssid", bssid = "bssid"
            )
        )
        val expectedWifiData = wifiSession.getSessionData("ssid")
        assertNotNull(expectedWifiData)
        assertEquals("ssid", expectedWifiData.ssid)
        assertEquals("bssid", expectedWifiData.bssid)
        assertEquals(0, expectedWifiData.rssi)
        assertEquals(0, expectedWifiData.frequency)
        assertEquals(0, expectedWifiData.linkSpeed)
        assertIsPositiveLong(wifiSession.getSessionDuration())
    }
}

private fun assertIsPositiveLong(time: Long) {
    assert(time > 0) { "Expected positive long value, but was $time" }
}
