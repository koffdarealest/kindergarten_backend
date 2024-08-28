package com.fsoft.fsa.kindergarten.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(String fileNamePrefix, MultipartFile file);

    String getLink(String fileName);
}
