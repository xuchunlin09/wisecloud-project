package com.wisecloud.mdm.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void success_wraps_data_with_200_code() {
        ApiResponse<String> response = ApiResponse.success("hello");

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.message()).isEqualTo("success");
        assertThat(response.data()).isEqualTo("hello");
    }

    @Test
    void success_with_null_data() {
        ApiResponse<Void> response = ApiResponse.success(null);

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.message()).isEqualTo("success");
        assertThat(response.data()).isNull();
    }

    @Test
    void error_wraps_code_and_message_with_null_data() {
        ApiResponse<Void> response = ApiResponse.error(404, "not found");

        assertThat(response.code()).isEqualTo(404);
        assertThat(response.message()).isEqualTo("not found");
        assertThat(response.data()).isNull();
    }
}
