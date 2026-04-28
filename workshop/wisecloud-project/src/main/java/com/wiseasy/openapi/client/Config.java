package com.wiseasy.openapi.client;

import lombok.Getter;
import lombok.Setter;

/**
 * SDK configuration holding AK/SK credentials and connection settings.
 */
@Getter
@Setter
public class Config {

    private String accessKeyId;
    private String accessKeySecret;
    private int connectTimeoutMs = 10000;
    private int readTimeoutMs = 30000;
    private int maxConnTotal = 200;
    private int maxConnPerRoute = 50;

    private static boolean sandBox = false;

    public Config(String accessKeyId, String accessKeySecret) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    public static void setSandBox(boolean sandBox) {
        Config.sandBox = sandBox;
    }

    public static boolean isSandBox() {
        return sandBox;
    }
}
