package project.healthcare_appointment.enums;

public enum FileUploadType {
    AVATAR("avatars"),
    DOCUMENT("documents");

    private final String directory;
    FileUploadType(String directory) {
        this.directory = directory;
    }
    public String getDirectory() {
        return directory;
    }
}
