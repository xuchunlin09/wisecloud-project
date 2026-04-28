package com.wisecloud.mdm.app.data.model

import org.junit.Assert.*
import org.junit.Test

class TaskDetailResponseTest {

    @Test
    fun `isAllTerminal returns true when executing and preparing are empty`() {
        val response = TaskDetailResponse(
            completed = listOf(TaskDeviceStatus("SN001", 3, null, null)),
            failed = listOf(TaskDeviceStatus("SN002", 4, "ERR01", "Install failed")),
            executing = emptyList(),
            preparing = emptyList()
        )
        assertTrue(response.isAllTerminal())
    }

    @Test
    fun `isAllTerminal returns false when executing is not empty`() {
        val response = TaskDetailResponse(
            completed = emptyList(),
            failed = emptyList(),
            executing = listOf(TaskDeviceStatus("SN001", 2, null, null)),
            preparing = emptyList()
        )
        assertFalse(response.isAllTerminal())
    }

    @Test
    fun `isAllTerminal returns false when preparing is not empty`() {
        val response = TaskDetailResponse(
            completed = emptyList(),
            failed = emptyList(),
            executing = emptyList(),
            preparing = listOf(TaskDeviceStatus("SN001", 1, null, null))
        )
        assertFalse(response.isAllTerminal())
    }

    @Test
    fun `isAllTerminal returns true when all lists are empty`() {
        val response = TaskDetailResponse(
            completed = emptyList(),
            failed = emptyList(),
            executing = emptyList(),
            preparing = emptyList()
        )
        assertTrue(response.isAllTerminal())
    }
}
