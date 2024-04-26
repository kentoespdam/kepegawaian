package id.perumdamts.kepegawaian.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UploadResultUtil implements Serializable {
    private boolean success;
    private String message;
    private String fileName;
    private String fileType;
    private String mimeType;
    private String hashedFileName;

    public UploadResultUtil(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static UploadResultUtil build(boolean success, String message) {
        return new UploadResultUtil(success, message);
    }

    public static UploadResultUtil build(boolean success, String message, String fileName, String fileType, String mimeType, String hashedFileName) {
        return new UploadResultUtil(success, message, fileName, fileType, mimeType, hashedFileName);
    }
}
