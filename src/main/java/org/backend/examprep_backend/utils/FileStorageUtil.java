package org.backend.examprep_backend.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileStorageUtil {

    public static void ensureDirectoryExists(Path directoryPath) {
        try {
            if (Files.notExists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create storage directory", e);
        }
    }
}

