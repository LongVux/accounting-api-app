package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.repositories.BranchAccountEntryRepository;
import com.outwork.accountingapiapp.repositories.GeneralAccountEntryRepository;
import com.outwork.accountingapiapp.repositories.ReceiptRepository;
import com.outwork.accountingapiapp.services.DataBackupService;
import com.outwork.accountingapiapp.services.FileStoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class FileDataController {

    @Autowired
    private FileStoringService fileStoringService;

    @Autowired
    private DataBackupService dataBackupService;

    @Autowired
    private GeneralAccountEntryRepository generalAccountEntryRepository;

    @Autowired
    private BranchAccountEntryRepository branchAccountEntryRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestBody MultipartFile file, @RequestParam Optional<String> imageId) throws IOException {
        String uploadedFileName = fileStoringService.uploadFile(file, imageId);
        return ResponseEntity.ok(uploadedFileName);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) throws IOException {
        byte[] file = fileStoringService.downloadFile(fileName);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/jpeg")).body(file);
    }

    @PutMapping("/backup")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> backupFiles() {
        dataBackupService.backupDatabase();
        dataBackupService.backupFolder();

        generalAccountEntryRepository.deleteAll();
        branchAccountEntryRepository.deleteAll();
        receiptRepository.deleteAll();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{fileName}")
    public void deleteFile(@PathVariable String fileName) throws IOException {
        fileStoringService.deleteFile(fileName);
    }
}