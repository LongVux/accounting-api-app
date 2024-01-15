package com.outwork.accountingapiapp.configs.schedule;

import com.outwork.accountingapiapp.services.DataBackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class SchedulerConfig {

    @Autowired
    private DataBackupService dataBackupService;

    @Scheduled(cron = "0 0 10 * * ?") // Run at 1:00 every day
    public void scheduleDatabaseBackup() {
        dataBackupService.backupDatabase();
    }

    @Scheduled(cron = "0 0 10 * * ?") // Run at 1:00 every day
    public void scheduleCleanupOldBackups() {
        dataBackupService.cleanupOldBackups();
    }
}
