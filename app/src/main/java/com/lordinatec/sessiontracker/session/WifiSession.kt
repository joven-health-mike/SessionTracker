package com.lordinatec.sessiontracker.session

import kotlinx.coroutines.flow.Flow

/**
 * Data class representing Wifi data
 */
data class WifiData(
    var ssid: String = "",
    var bssid: String = "",
    var rssi: Int = 0,
    var frequency: Int = 0,
    var linkSpeed: Int = 0,
    var ip: String = "",
    var mac: String = ""
)

/**
 * Sealed class representing Wifi events
 */
sealed class WifiEvent {
    /**
     * Data class representing a Wifi connected event
     */
    data class WifiConnected(
        val ssid: String,
        val bssid: String,
        val rssi: Int,
        val frequency: Int,
        val linkSpeed: Int
    ) : WifiEvent()

    /**
     * Data class representing a Wifi disconnected event
     */
    data class WifiDisconnected(val ssid: String, val bssid: String) : WifiEvent()

    /**
     * Data class representing a Wifi signal strength changed event
     */
    data class WifiSignalStrengthChanged(val rssi: Int) : WifiEvent()

    /**
     * Data class representing a Wifi link speed changed event
     */
    data class WifiLinkSpeedChanged(val linkSpeed: Int) : WifiEvent()

    /**
     * Data class representing a Wifi frequency changed event
     */
    data class WifiFrequencyChanged(val frequency: Int) : WifiEvent()

    /**
     * Data class representing a Wifi IP changed event
     */
    data class WifiIpChanged(val ip: String) : WifiEvent()

    /**
     * Data class representing a Wifi MAC changed event
     */
    data class WifiMacChanged(val mac: String) : WifiEvent()
}

/**
 * Class representing a Wifi session
 *
 * @property sessionId The session ID
 * @property wifiEventProvider The flow of Wifi events
 *
 * @constructor Creates a new WifiSession
 */
class WifiSession(
    override val sessionId: String,
    private val wifiEventProvider: Flow<WifiEvent>
) :
    DataSession<WifiData> {
    override val data: MutableMap<String, WifiData> = mutableMapOf()
    override var isActive: Boolean = false
    override var startTime: Long = -1L
    override var endTime: Long = -1L

    private val _wifiData = WifiData()

    /**
     * Listens for Wifi events
     */
    suspend fun listenForWifiEvents() {
        wifiEventProvider.collect { wifiEvent ->
            when (wifiEvent) {
                is WifiEvent.WifiConnected -> {
                    start()
                    _wifiData.ssid = wifiEvent.ssid
                    _wifiData.bssid = wifiEvent.bssid
                    _wifiData.rssi = wifiEvent.rssi
                    _wifiData.frequency = wifiEvent.frequency
                    _wifiData.linkSpeed = wifiEvent.linkSpeed
                    addSessionData(wifiEvent.ssid, _wifiData)
                }

                is WifiEvent.WifiDisconnected -> {
                    stop()
                }

                is WifiEvent.WifiSignalStrengthChanged -> {
                    getSessionData(_wifiData.ssid)?.rssi = wifiEvent.rssi
                }

                is WifiEvent.WifiLinkSpeedChanged -> {
                    getSessionData(_wifiData.ssid)?.linkSpeed = wifiEvent.linkSpeed
                }

                is WifiEvent.WifiFrequencyChanged -> {
                    getSessionData(_wifiData.ssid)?.frequency = wifiEvent.frequency
                }

                is WifiEvent.WifiIpChanged -> {
                    getSessionData(_wifiData.ssid)?.ip = wifiEvent.ip
                }

                is WifiEvent.WifiMacChanged -> {
                    getSessionData(_wifiData.ssid)?.mac = wifiEvent.mac
                }
            }
        }
    }
}
