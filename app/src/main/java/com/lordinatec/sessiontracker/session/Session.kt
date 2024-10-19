package com.lordinatec.sessiontracker.session

interface Session {
    var isActive: Boolean
    val sessionId: String
    var startTime: Long
    var endTime: Long

    fun start() {
        if (!isActive) {
            isActive = true
            startTime = System.currentTimeMillis()
        }
    }

    fun stop() {
        if (isActive) {
            isActive = false
            endTime = System.currentTimeMillis()
        }
    }

    fun getSessionDuration(): Long {
        return endTime - startTime
    }
}

interface DataSession<D> : Session {
    val data: MutableMap<String, D>

    fun getSessionData(): Map<String, D> {
        return data.toMap()
    }

    fun getSessionData(key: String): D? {
        return data[key]
    }

    fun addSessionData(key: String, value: D) {
        data[key] = value
    }

    fun removeSessionData(key: String) {
        data.remove(key)
    }

    fun clearSessionData() {
        data.clear()
    }
}
