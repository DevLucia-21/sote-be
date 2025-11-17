package com.fluxion.sote.global.config;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class GcpConfig {

    @Value("${spring.cloud.gcp.storage.credentials.location}")
    private String credentialsPath;

    @Value("${spring.cloud.gcp.storage.project-id}")
    private String projectId;

    @Bean
    public Storage storage() throws IOException {

        // file: prefix 제거 (중요)
        String realPath = credentialsPath.replace("file:", "");

        System.out.println("GCP Storage Credentials Path = " + realPath);

        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(realPath)))
                .build()
                .getService();
    }
}
