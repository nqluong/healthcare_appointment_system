package project.healthcare_appointment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import project.healthcare_appointment.dto.response.FileUploadResponse;
import project.healthcare_appointment.enums.FileUploadType;
import project.healthcare_appointment.exception.AppException;
import project.healthcare_appointment.exception.ErrorCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {
    @Value("${file.upload.base-path:uploads}")
    private String basePath;

    @Value("${file.upload.max-size:5242880}")
    private long maxFileSize;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );

    @Override
    public FileUploadResponse uploadFile(MultipartFile file, FileUploadType fileType) {
        try {
            validateFile(file);

            String fileName = generateFileName(file);
            String folderPath = createFolderIfNotExists(fileType);
            Path filePath = Paths.get(folderPath, fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/" + fileType.getDirectory() + "/" + fileName;

            log.info("File uploaded successfully: {}", fileUrl);

            return FileUploadResponse.builder()
                    .success(true)
                    .fileUrl(fileUrl)
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .message("File uploaded successfully")
                    .build();

        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return;
        }

        try {
            Path filePath = Paths.get(basePath + fileUrl);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted successfully: {}", fileUrl);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileUrl, e);
            throw new AppException(ErrorCode.FILE_DELETE_FAILED, e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }

        if (file.getSize() > maxFileSize) {
            throw new AppException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        String contentType = file.getContentType();
        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    private String generateFileName(MultipartFile file) {
        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getFileExtension(originalName);
        return UUID.randomUUID().toString() + extension;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }

    private String createFolderIfNotExists(FileUploadType fileType) throws IOException {
        String folderPath = basePath + "/" + fileType.getDirectory();
        Path path = Paths.get(folderPath);

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        return folderPath;
    }
}
