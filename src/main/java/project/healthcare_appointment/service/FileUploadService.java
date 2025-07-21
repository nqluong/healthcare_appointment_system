package project.healthcare_appointment.service;

import org.springframework.web.multipart.MultipartFile;
import project.healthcare_appointment.dto.response.FileUploadResponse;
import project.healthcare_appointment.enums.FileUploadType;

public interface FileUploadService {
    /**
     * Uploads a file to the server.
     *
     * @param file the file to upload
     * @param fileType the type of the file (e.g., AVATAR, DOCUMENT)
     * @return a response containing the upload status and file details
     */
    FileUploadResponse uploadFile(MultipartFile file, FileUploadType fileType) ;

    /**
     * Deletes a file from the server.
     *
     * @param fileUrl the URL of the file to delete
     */
    void deleteFile(String fileUrl);
}
