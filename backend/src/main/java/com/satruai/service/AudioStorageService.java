package com.satruai.service;

import java.io.IOException;

/**
 * Interface for audio file storage management
 */
public interface AudioStorageService {

    /**
     * Get configured storage path
     */
    String getStoragePath();

    /**
     * Initialize storage directory
     */
    void initializeStorage() throws IOException;

    /**
     * Get total storage usage in bytes
     */
    long getStorageUsage() throws IOException;

    /**
     * Clean up old files based on retention policy
     */
    void cleanupOldFiles() throws IOException;

    /**
     * Validate file size
     */
    boolean validateFileSize(long fileSize);

    /**
     * Delete a file
     */
    void deleteFile(String filePath) throws IOException;

    /**
     * Check if file exists
     */
    boolean fileExists(String filePath);
}

