package com.lordinatec.sessiontracker.session

import kotlinx.coroutines.flow.Flow

data class WifiData(
    var ssid: String = "",
    var bssid: String = "",
    var rssi: Int = 0,
    var frequency: Int = 0,
    var linkSpeed: Int = 0,
    var ip: String = "",
    var mac: String = ""
)

sealed class WifiEvent {
    data class WifiConnected(
        val ssid: String,
        val bssid: String,
        val rssi: Int,
        val frequency: Int,
        val linkSpeed: Int
    ) : WifiEvent()

    data class WifiDisconnected(val ssid: String, val bssid: String) : WifiEvent()
    data class WifiSignalStrengthChanged(val rssi: Int) : WifiEvent()
    data class WifiLinkSpeedChanged(val linkSpeed: Int) : WifiEvent()
    data class WifiFrequencyChanged(val frequency: Int) : WifiEvent()
    data class WifiIpChanged(val ip: String) : WifiEvent()
    data class WifiMacChanged(val mac: String) : WifiEvent()
}

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
