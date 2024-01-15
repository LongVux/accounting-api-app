package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.constants.DataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
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
        try (Connection connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword)) {
            DatabaseMetaData metaData = connection.getMetaData();

            try (FileWriter fileWriter = new FileWriter(String.format("db_backup_%s.sql", LocalDate.now().format(DateTimeFormatter.ofPattern(DataFormat.DATE_FORMAT_ddMMyy))));
                 PrintWriter printWriter = new PrintWriter(fileWriter)) {

                // Get all table names in the database
                ResultSet tableResultSet = metaData.getTables(null, connection.getCatalog(), "%accounting_app_db%", null);
                while (tableResultSet.next()) {
                    String tableName = tableResultSet.getString("TABLE_NAME");
                    backupTable(connection, tableName, printWriter);
                }

                log.info("Database backup completed successfully.");

            } catch (Exception e) {
                log.error("Cannot backup database", e);
            }

        } catch (Exception e) {
            log.error("Cannot connect database", e);
        }
    }

    private static void backupTable(Connection connection, String tableName, PrintWriter printWriter) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);

            printWriter.println("-- Data for table: " + tableName);
            printWriter.println("INSERT INTO " + tableName + " VALUES ");

            int rowCount = 0;

            while (resultSet.next()) {
                if (rowCount > 0) {
                    printWriter.print(", ");
                }

                printWriter.print("(");

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    printWriter.print("'" + resultSet.getString(i) + "'");
                    if (i < resultSet.getMetaData().getColumnCount()) {
                        printWriter.print(", ");
                    }
                }

                printWriter.print(")");

                rowCount++;

                if (rowCount % 1000 == 0) {
                    // Flush the batch to the file
                    printWriter.println(";");
                    printWriter.flush();
                    rowCount = 0;
                }
            }

            printWriter.println(";");
            printWriter.flush();

            log.info("Table '" + tableName + "' backup completed.");

        } catch (Exception e) {
            log.error("Error backup table", e);
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
        }
    }

    public void backupFolder() {
        try {
            String zipFileName = "img_backup_" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ".zip";

            zipFolder(fileDirectory, backupDirectory);
            System.out.println("Backup successful. Zip file created: " + backupDirectory);
        } catch (IOException e) {
            log.error("Error during backup", e);
        }
    }


    private void zipFolder(String sourceFolderPath, String zipFilePath) throws IOException {
        try (
                FileOutputStream fos = new FileOutputStream(new File(zipFilePath).getAbsolutePath());
                ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos))
        ) {
            File sourceFolder = new File(sourceFolderPath);
            addFolderToZip(sourceFolder, sourceFolder.getName(), zos);
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
                }
            }
        }
    }
}
