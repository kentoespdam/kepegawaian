package id.perumdamts.kepegawaian.utils;

public interface MimeTypesUtils {
    String getExtension(String mimeType);
    boolean isImage(String mimeType);
    boolean isPdf(String mimeType);
    boolean isWord(String mimeType);
    boolean isExcel(String mimeType);
    boolean isPowerpoint(String mimeType);
    boolean isDocument(String mimeType);
    String isSupported(String mimeType);
    String isSupportedExcel(String mimeType);
}
