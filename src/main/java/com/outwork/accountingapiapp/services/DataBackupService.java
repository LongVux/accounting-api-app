package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.text.StringContent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DataBackupService {

    private static final Logger log = LoggerFactory.getLogger(DataBackupService.class);

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Value("${backup.directory}")
    private String backupDirectory;

    @Value("${file.storage.path}")
    private String fileDirectory;

    public void backupDatabase() {
        String backupFileName = String.format("db_backup_haha_%s.sql", LocalDate.now().format(DateTimeFormatter.ofPattern(DataFormat.DATE_FORMAT_ddMMyy)));

        String command = String.format("C:\\Program Files\\MySQL\\MySQL Workbench 8.0\\mysqldump.exe -u %s -p%s %s --result-file=%s\\%s --default-character-set=utf8",
                databaseUsername, databasePassword, "accounting_app_db", backupDirectory, backupFileName);

        System.out.println(command);

        try {
            Process process = Runtime.getRuntime().exec(command);
            // Capture and log the output stream
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Capture and log the error stream
            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Database backup successful.");
            } else {
                throw new RuntimeException("Fail to backup");
            }

        } catch (Exception e) {
            log.error("Error executing backup command: ", e);
            throw new InvalidDataException("Hệ thống không thể thực hiện backup cơ sở dữ liệu");
        }
    }

    public void cleanupOldBackups() {
        try {
            File backupFolder = new File(backupDirectory);

            // Calculate the date one month ago
            String oneMonthAgo = LocalDate.now().withMonth(LocalDate.now().getMonthValue() - 1).format(DateTimeFormatter.ofPattern(DataFormat.DATE_FORMAT_ddMMyy));

            // Iterate through the files in the backup folder
            for (File file : Objects.requireNonNull(backupFolder.listFiles())) {
                if (file.isFile() && file.getName().endsWith(".sql") && file.getName().contains(oneMonthAgo)) {
                    if (file.delete()) {
                        log.info("Delete old backup file successfully: " + file.getName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception during delete old backup file", e);
            throw new InvalidDataException("Hệ thống không thể thực hiện dọn dẹp backup cơ sở dữ liệu đã quá hạn");
        }
    }

    public void backupFolder() {
        try {
            String zipFileName = "img_backup_" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ".zip";

            zipFolder(fileDirectory, String.join(DataFormat.BACKSLASH_SEPARATOR, backupDirectory, zipFileName));
            log.info("Backup successful. Zip file created: " + backupDirectory);
        } catch (IOException e) {
            log.error("Error during backup", e);
            throw new InvalidDataException("Hệ thống không thể thực hiện backup tệp ảnh");
        }
    }


    private void zipFolder(String sourceFolderPath, String zipFilePath) throws IOException {
        try (
                FileOutputStream fos = new FileOutputStream(new File(zipFilePath).getAbsolutePath());
                ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos))
        ) {
            File sourceFolder = new File(sourceFolderPath);
            addFolderToZip(sourceFolder, sourceFolder.getName(), zos);
        } catch (Exception e) {
            log.error("Error during zip file", e);
            throw new InvalidDataException("Hệ thống không thể nén backup tệp ảnh");
        }
    }

    private void addFolderToZip(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                addFolderToZip(file, parentFolder + File.separator + file.getName(), zos);
            } else {
                try (
                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))
                ) {
                    ZipEntry zipEntry = new ZipEntry(parentFolder + File.separator + file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = bis.read(bytes)) >= 0) {
                        zos.write(bytes, 0, length);
                    }

                    zos.closeEntry();
                } catch (Exception e) {
                    log.error("Error during zip file", e);
                    throw new InvalidDataException("Hệ thống không thể nén backup tệp ảnh");
                }
            }
        }
    }
}
