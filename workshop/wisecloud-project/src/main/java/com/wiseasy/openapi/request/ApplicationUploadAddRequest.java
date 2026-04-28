package com.wiseasy.openapi.request;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

/**
 * Request for application/upload/add API — upload and add an APK application.
 */
@Getter
@Setter
public class ApplicationUploadAddRequest {

    private String version = "v1.0";
    private String appAlias;
    private String appDescription;
    private byte[] fileData;
    private String fileName;
    private File file;

    /**
     * Set file data as byte array with a file name.
     */
    public void putByteArrayFile(String fileName, byte[] fileData) {
        this.fileName = fileName;
        this.fileData = fileData;
    }

    /**
     * Set file from a File object.
     */
    public void putFile(String fileName, File file) {
        this.fileName = fileName;
        this.file = file;
    }
}
