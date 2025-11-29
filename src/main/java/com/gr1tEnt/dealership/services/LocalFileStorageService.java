package com.gr1tEnt.dealership.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path rootLocation;

    public LocalFileStorageService(@Value("${app.storage.location}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir);
        init();
    }

    private void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @Override
    public String save(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Failed to store empty file.");
            }

            String fileName = UUID.randomUUID().toString();

            Path filePath = rootLocation.resolve(fileName)
                    .normalize().toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            }
            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Could not store file", e);
        }

    }
}
