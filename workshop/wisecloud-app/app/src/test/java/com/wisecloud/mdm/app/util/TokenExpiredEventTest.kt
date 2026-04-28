package com.wisecloud.mdm.app.util

import org.junit.After
import org.junit.Assert.*
import org.junit.Test

class TokenExpiredEventTest {

    private val registeredListeners = mutableListOf<() -> Unit>()

    @After
    fun tearDown() {
        registeredListeners.forEach { TokenExpiredEvent.unregister(it) }
        registeredListeners.clear()
    }

    @Test
    fun `post notifies registered listener`() {
        var called = false
        val listener: () -> Unit = { called = true }
        registeredListeners.add(listener)
        TokenExpiredEvent.register(listener)

        TokenExpiredEvent.post()

        assertTrue(called)
    }

    @Test
    fun `unregister prevents listener from being called`() {
        var called = false
        val listener: () -> Unit = { called = true }
        TokenExpiredEvent.register(listener)
        TokenExpiredEvent.unregister(listener)

        TokenExpiredEvent.post()

        assertFalse(called)
    }

    @Test
    fun `post notifies multiple listeners`() {
        var count = 0
        val listener1: () -> Unit = { count++ }
        val listener2: () -> Unit = { count++ }
        registeredListeners.add(listener1)
        registeredListeners.add(listener2)
        TokenExpiredEvent.register(listener1)
        TokenExpiredEvent.register(listener2)

        TokenExpiredEvent.post()

        assertEquals(2, count)
    }
}
