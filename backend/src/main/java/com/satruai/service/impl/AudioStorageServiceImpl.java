package com.satruai.service.impl;

import com.satruai.service.AudioStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Service for managing audio file storage
 * Handles file saving, retrieval, and cleanup
 */
@Slf4j
@Service
public class AudioStorageServiceImpl implements AudioStorageService {

    @Value("${voice.storage.path:./audio}")
    private String storagePath;

    @Value("${voice.storage.max-file-size:26214400}")
    private Long maxFileSize;

    @Value("${voice.storage.cleanup-enabled:true}")
    private Boolean cleanupEnabled;

    @Value("${voice.storage.retention-days:30}")
    private Integer retentionDays;

    @Override
    public String getStoragePath() {
        return storagePath;
    }

    @Override
    public void initializeStorage() throws IOException {
        Path path = Paths.get(storagePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("Created audio storage directory: {}", storagePath);
        }
    }

    @Override
    public long getStorageUsage() throws IOException {
        Path path = Paths.get(storagePath);
        if (!Files.exists(path)) {
            return 0;
        }

        return Files.walk(path)
                .map(p -> {
                    try {
                        return Files.size(p);
                    } catch (IOException e) {
                        log.warn("Error getting file size: {}", p, e);
                        return 0L;
                    }
                })
                .reduce(0L, Long::sum);
    }

    @Override
    public void cleanupOldFiles() throws IOException {
        if (!cleanupEnabled) {
            log.debug("Cleanup disabled");
            return;
        }

        Path path = Paths.get(storagePath);
        if (!Files.exists(path)) {
            return;
        }

        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(retentionDays);

        Files.walk(path)
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    try {
                        LocalDateTime fileTime = LocalDateTime.ofInstant(
                                Files.getLastModifiedTime(filePath).toInstant(),
                                java.time.ZoneId.systemDefault()
                        );

                        if (fileTime.isBefore(cutoffTime)) {
                            Files.delete(filePath);
                            log.info("Deleted old audio file: {}", filePath);
                        }
                    } catch (IOException e) {
                        log.warn("Error deleting old file: {}", filePath, e);
                    }
                });
    }

    @Override
    public boolean validateFileSize(long fileSize) {
        return fileSize > 0 && fileSize <= maxFileSize;
    }

    @Override
    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
            log.info("Deleted audio file: {}", filePath);
        }
    }

    @Override
    public boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}

