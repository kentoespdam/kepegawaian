package id.perumdamts.kepegawaian.utils;

public interface MimeTypesUtils {
    String getExtension(String mimeType);
    boolean isImage(String mimeType);
    String isSupported(String mimeType);
}
