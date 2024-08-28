package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.service.FileStorageService;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImp implements FileStorageService {

    private final Bucket bucket;
    private final Storage storage;

    public String storeFile(String fileNamePrefix, MultipartFile file) {

        String fileName = fileNamePrefix + "_" + file.getOriginalFilename();

        try {
            BlobInfo blobInfo = bucket.create(fileName, file.getInputStream(), file.getContentType());
            return blobInfo.getMediaLink();
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public void deleteFile(String fileName) {
        Blob blob = storage.get(bucket.getName(), fileName);
        blob.delete();
    }

    public String getLink(String fileName) {
        Blob blob = storage.get(bucket.getName(), fileName);
        return blob.getSelfLink();
    }
}
