package com.wisecloud.mdm.app.util

/**
 * Simple event bus for token expiration notification.
 * When a 401 response is received, AuthInterceptor posts this event
 * so the UI layer can redirect to the login screen.
 */
object TokenExpiredEvent {

    private val listeners = mutableListOf<() -> Unit>()

    fun register(listener: () -> Unit) {
        synchronized(listeners) {
            listeners.add(listener)
        }
    }

    fun unregister(listener: () -> Unit) {
        synchronized(listeners) {
            listeners.remove(listener)
        }
    }

    fun post() {
        synchronized(listeners) {
            listeners.forEach { it.invoke() }
        }
    }
}
