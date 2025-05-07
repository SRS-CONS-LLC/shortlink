package com.srscons.shortlink.linkinbio.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

public class FileUploadUtil {

        private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".pdf");

        public static String saveFile(String uploadDir, MultipartFile multipartFile) {
            String originalFileName = multipartFile.getOriginalFilename();

            if (originalFileName == null || !originalFileName.contains(".")) {
                throw new IllegalArgumentException("Invalid file: no extension found.");
            }

            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();

            if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
                throw new IllegalArgumentException("File type not allowed: " + fileExtension);
            }

            if (multipartFile.getSize() > 20 * 1024 * 1024) { // 5MB limit
                throw new IllegalArgumentException("File size exceeds the 5MB limit.");
            }

            String savedFileName = UUID.randomUUID() + fileExtension;

            try {
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = multipartFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(savedFileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                return savedFileName;
            } catch (IOException e) {
                throw new RuntimeException("Could not save file: " + originalFileName, e);
            }
        }
    }

