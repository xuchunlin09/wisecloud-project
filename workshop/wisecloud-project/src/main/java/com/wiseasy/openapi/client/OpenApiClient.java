package com.wiseasy.openapi.client;

/**
 * WiseCloud OpenAPI client interface.
 * Stub interface matching the real SDK's OpenApiClient.
 */
public interface OpenApiClient {

    /**
     * Execute an API request and return the typed response.
     *
     * @param request the API request object
     * @param <T>     the response type
     * @return the API response
     * @throws OpenApiClientException if an SDK-level error occurs (codes -1001 to -1006)
     */
    <T> T execute(Object request) throws OpenApiClientException;
}
