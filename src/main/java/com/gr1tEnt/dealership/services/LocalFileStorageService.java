package com.gr1tEnt.dealership.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalFileStorageService {

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
}
