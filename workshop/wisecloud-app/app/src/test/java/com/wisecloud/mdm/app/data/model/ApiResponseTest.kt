package com.wisecloud.mdm.app.data.model

import org.junit.Assert.*
import org.junit.Test

class ApiResponseTest {

    @Test
    fun `isSuccess returns true for code 200`() {
        val response = ApiResponse(200, "success", "data")
        assertTrue(response.isSuccess)
    }

    @Test
    fun `isSuccess returns false for non-200 code`() {
        val response = ApiResponse<String>(401, "Unauthorized", null)
        assertFalse(response.isSuccess)
    }

    @Test
    fun `data can be null`() {
        val response = ApiResponse<String>(200, "success", null)
        assertTrue(response.isSuccess)
        assertNull(response.data)
    }
}
