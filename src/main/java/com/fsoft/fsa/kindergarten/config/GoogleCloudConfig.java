package com.fsoft.fsa.kindergarten.config;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleCloudConfig {

    @Value("${gcp.projectId}")
    private String projectId;

    @Bean
    public Storage storage() {
        return StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    }

    @Bean
    public Bucket bucket(@Value("${gcp.bucketName}") String bucketName, Storage storage) {
        return storage.get(bucketName);
    }
}
