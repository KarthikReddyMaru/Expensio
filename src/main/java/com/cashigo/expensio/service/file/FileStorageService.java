package com.cashigo.expensio.service.file;

import com.cashigo.expensio.common.consts.Status.FileStatus;
import com.cashigo.expensio.model.FileMetaData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${file.storage.max-size}")
    private Long maxFileSize;

    @Value("${file.storage.path}")
    private String fileStoragePath;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "csv", "xlsx", "xls", "pdf", "txt"
    );

    @SuppressWarnings(value = "path-traversal")
    public FileMetaData storeFile(MultipartFile multipartFile) throws IOException {

        if (multipartFile.getSize() > maxFileSize)
            return null;

        Path uploadDir = Paths.get(fileStoragePath).toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);

        String originalFileName = sanitizeFilename(multipartFile.getOriginalFilename());
        String fileName = generateUniqueName(originalFileName);
        Path filePath = uploadDir.resolve(fileName).normalize();

        if (!filePath.startsWith(uploadDir)) {
            throw new SecurityException("Path traversal attempt detected");
        }

        Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return FileMetaData.builder()
                .originalFileName(multipartFile.getOriginalFilename())
                .storedFileName(fileName)
                .filePath(filePath.toString())
                .fileSize(multipartFile.getSize())
                .expiresAt(Instant.now().plus(Duration.ofDays(1)))
                .status(FileStatus.ACTIVE)
                .build();
    }

    /**
     * Sanitize filename by removing dangerous characters and path separators
     */
    private String sanitizeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "unnamed_file";
        }

        // ✅ Remove path separators and traversal attempts
        filename = filename.replaceAll("[/\\\\]+", "_");  // Replace / and \ with _

        // ✅ Remove special characters that could be problematic
        filename = filename.replaceAll("[<>:\"|?*]", "_");

        // ✅ Remove leading/trailing dots and spaces
        filename = filename.replaceAll("^\\.+|\\.+$", "");  // Remove leading/trailing dots
        filename = filename.trim();

        // ✅ Limit length
        if (filename.length() > 255) {
            filename = filename.substring(0, 255);
        }

        // ✅ Ensure at least some name
        if (filename.isEmpty()) {
            filename = "file";
        }

        return filename;
    }

    private String generateUniqueName(String originalFileName) {
        String fileExtension = getFileExtension(originalFileName);
        String timeStamp = Instant.now().toString();
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.join("_", fileExtension, timeStamp, uuid);
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        String extension = lastDot > 0 ? fileName.substring(lastDot + 1) : "";
        return ALLOWED_EXTENSIONS.contains(extension) ? extension : "tmp";
    }

}
