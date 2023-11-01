package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.services.FileStoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileDataController {

    @Autowired
    private FileStoringService fileStoringService;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestBody MultipartFile file) throws IOException {
        String uploadedFileName = fileStoringService.uploadFile(file);
        return ResponseEntity.ok(uploadedFileName);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) throws IOException {
        byte[] file = fileStoringService.downloadFile(fileName);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/jpeg")).body(file);
    }

    @DeleteMapping("/{fileName}")
    public void deleteFile(@PathVariable String fileName) throws IOException {
        fileStoringService.deleteFile(fileName);
    }
}