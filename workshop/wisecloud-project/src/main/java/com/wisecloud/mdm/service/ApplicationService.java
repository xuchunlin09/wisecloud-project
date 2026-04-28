package com.wisecloud.mdm.service;

import com.wisecloud.mdm.dto.response.AppUploadResponse;
import com.wisecloud.mdm.dto.response.AppUploadResult;
import com.wisecloud.mdm.dto.response.ApplicationInfo;
import com.wisecloud.mdm.entity.Application;
import com.wisecloud.mdm.repository.ApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Application management business logic: upload APK, list uploaded applications.
 */
@Service
public class ApplicationService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationService.class);

    private final WiseCloudService wiseCloudService;
    private final ApplicationRepository applicationRepository;

    public ApplicationService(WiseCloudService wiseCloudService,
                              ApplicationRepository applicationRepository) {
        this.wiseCloudService = wiseCloudService;
        this.applicationRepository = applicationRepository;
    }

    /**
     * Upload an APK file to WiseCloud and persist the application metadata locally.
     *
     * @param file           the APK multipart file
     * @param appAlias       application alias
     * @param appDescription optional application description
     * @return upload response with versionMD5, versionNumber, appName, packageName
     */
    public AppUploadResponse uploadApk(MultipartFile file, String appAlias,
                                       String appDescription) throws IOException {
        byte[] fileData = file.getBytes();
        String fileName = file.getOriginalFilename();

        // Call WiseCloud SDK to upload the APK
        AppUploadResult result = wiseCloudService.uploadApplication(
                fileData, fileName, appAlias, appDescription);

        // Persist application metadata to local DB
        Application application = new Application();
        application.setAppName(result.getAppName());
        application.setAppPackage(result.getAppPackage());
        application.setVersionNumber(result.getVersionNumber());
        application.setVersionMd5(result.getVersionMD5());
        application.setAppAlias(appAlias);
        application.setAppDescription(appDescription);
        applicationRepository.save(application);

        log.info("APK uploaded successfully: appName={}, package={}, versionMD5={}",
                result.getAppName(), result.getAppPackage(), result.getVersionMD5());

        return new AppUploadResponse(
                result.getVersionMD5(),
                result.getVersionNumber(),
                result.getAppName(),
                result.getAppPackage()
        );
    }

    /**
     * List all uploaded applications from the local repository.
     *
     * @return list of application info DTOs
     */
    public List<ApplicationInfo> listApplications() {
        return applicationRepository.findAll().stream()
                .map(app -> new ApplicationInfo(
                        app.getId(),
                        app.getAppName(),
                        app.getAppPackage(),
                        app.getVersionNumber(),
                        app.getVersionName(),
                        app.getVersionMd5(),
                        app.getAppAlias(),
                        app.getAppDescription(),
                        app.getUploadedAt()
                ))
                .collect(Collectors.toList());
    }
}
