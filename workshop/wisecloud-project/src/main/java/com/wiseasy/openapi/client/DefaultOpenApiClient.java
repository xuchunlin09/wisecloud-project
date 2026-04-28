package com.wiseasy.openapi.client;

/**
 * Default implementation of OpenApiClient.
 * In production, this is provided by the real WiseCloud SDK JAR.
 */
public class DefaultOpenApiClient implements OpenApiClient {

    private final Config config;

    public DefaultOpenApiClient(Config config) {
        this.config = config;
    }

    @Override
    public <T> T execute(Object request) throws OpenApiClientException {
        throw new UnsupportedOperationException(
                "DefaultOpenApiClient.execute() is a stub. Install the real WiseCloud SDK JAR.");
    }
}
