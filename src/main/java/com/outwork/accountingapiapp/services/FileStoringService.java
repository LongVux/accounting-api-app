package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.models.entity.FileDataEntity;
import com.outwork.accountingapiapp.repositories.FileDataRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStoringService {

    @Autowired
    private FileDataRepository fileDataRepository;

    @Value("${file.storage.path}")
    private String appBucketPath;

    @Transactional(rollbackFor = {IOException.class})
    public String uploadFile(MultipartFile file, Optional<String> imageId) throws IOException {
        String fileName = imageId.orElse(UUID.randomUUID().toString());

        FileDataEntity imageData = fileDataRepository.save(FileDataEntity.builder()
                .fileName(fileName)
                .fileType(file.getContentType())
                .build()
        );

        File createdFile = new File(String.join(DataFormat.BACKSLASH_SEPARATOR, appBucketPath, imageData.getFileName()));

        file.transferTo(createdFile.getAbsoluteFile());

        return imageData.getFileName();
    }

    public byte[] downloadFile(String fileName) throws IOException {
        FileDataEntity fileData = fileDataRepository.findById(fileName).orElseThrow(() -> new EntityNotFoundException(
                fileName));

        return Files.readAllBytes(new File(String.join(DataFormat.BACKSLASH_SEPARATOR, appBucketPath, fileData.getFileName())).toPath());
    }

    public void deleteFile(String fileName) throws IOException {
        FileDataEntity fileData = fileDataRepository.findById(fileName).orElseThrow(() -> new EntityNotFoundException(
               fileName));

        Files.delete(new File(String.join(DataFormat.BACKSLASH_SEPARATOR, appBucketPath, fileData.getFileName())).toPath());
    }
}
