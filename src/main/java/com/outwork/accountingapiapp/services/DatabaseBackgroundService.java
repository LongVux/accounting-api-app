package com.outwork.accountingapiapp.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DatabaseBackgroundService {

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.name}")
    private String dbName;

    public void backupDatabase(String backupFileName) throws IOException {
        // Use mysqldump to backup the MySQL database
        String command = "mysqldump -u yourusername -pyourpassword yourdatabase > " + backupFileName;
        Process process = Runtime.getRuntime().exec(command);

        // Wait for the process to finish
        try {
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Backup successful!");
            } else {
                System.err.println("Backup failed!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
