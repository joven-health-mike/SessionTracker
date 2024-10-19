package com.lordinatec.sessiontracker.session

/**
 * Interface representing some kind of session.
 */
interface Session {
    /**
     * Whether the session is active.
     */
    var isActive: Boolean

    /**
     * The unique identifier for the session.
     */
    val sessionId: String

    /**
     * The start and end times of the session.
     */
    var startTime: Long
    var endTime: Long

    /**
     * Starts the session.
     */
    fun start() {
        if (!isActive) {
            isActive = true
            startTime = System.currentTimeMillis()
        }
    }

    /**
     * Stops the session.
     */
    fun stop() {
        if (isActive) {
            isActive = false
            endTime = System.currentTimeMillis()
        }
    }

    /**
     * Gets the duration of the session.
     */
    fun getSessionDuration(): Long {
        return endTime - startTime
    }
}

/**
 * Interface representing a session that contains generic data.
 */
interface DataSession<D> : Session {
    /**
     * The data associated with the session.
     */
    val data: MutableMap<String, D>

    /**
     * Gets the data map associated with the session.
     */
    fun getSessionData(): Map<String, D> {
        return data.toMap()
    }

    /**
     * Gets the data associated with the session for the given key.
     */
    fun getSessionData(key: String): D? {
        return data[key]
    }

    /**
     * Adds data to the session.
     */
    fun addSessionData(key: String, value: D) {
        data[key] = value
    }

    /**
     * Removes data from the session.
     */
    fun removeSessionData(key: String) {
        data.remove(key)
    }

    /**
     * Clears all data from the session.
     */
    fun clearSessionData() {
        data.clear()
    }
}
