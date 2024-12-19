package id.perumdamts.kepegawaian.utils;

import org.springframework.stereotype.Service;

@Service
public class MimeTypesUtilsImpl implements MimeTypesUtils {
    private final String[] imageMimeTypes = {
            "image/jpeg",
            "image/png",
    };

    private final String[] pdfMimeTypes = {
            "application/pdf",
    };

    private final String[] wordMimeTypes = {
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    };

    private final String[] excelMimeTypes = {
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.oasis.opendocument.spreadsheet",
    };

    private final String[] powerpointMimeTypes = {
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.oasis.opendocument.presentation"
    };

    private final String[] compressedMimeTypes = {
            "application/zip",
            "application/x-7z-compressed",
            "application/x-rar-compressed",
            "application/x-tar",
            "application/gzip"
    };

    @Override
    public boolean isImage(String mimeType) {
        for (String imageMimeType : imageMimeTypes) {
            if (imageMimeType.equals(mimeType)) return true;
        }
        return false;
    }

    @Override
    public boolean isPdf(String mimeType) {
        for (String pdfMimeType : pdfMimeTypes) {
            if (pdfMimeType.equals(mimeType)) return true;
        }
        return false;
    }

    @Override
    public boolean isWord(String mimeType) {
        for (String wordMimeType : wordMimeTypes) {
            if (wordMimeType.equals(mimeType)) return true;
        }
        return false;
    }

    @Override
    public boolean isExcel(String mimeType) {
        for (String excelMimeType : excelMimeTypes) {
            if (excelMimeType.equals(mimeType)) return true;
        }
        return false;
    }

    @Override
    public boolean isPowerpoint(String mimeType) {
        for (String powerpointMimeType : powerpointMimeTypes) {
            if (powerpointMimeType.equals(mimeType)) return true;
        }
        return false;
    }

    public boolean isDocument(String mimeType) {
        return isPdf(mimeType) || isWord(mimeType) ||
                isExcel(mimeType) || isPowerpoint(mimeType);
    }

    public boolean isCompressed(String mimeType) {
        for (String compressedMimeType : compressedMimeTypes) {
            if (compressedMimeType.equals(mimeType)) return true;
        }
        return false;
    }

    @Override
    public String getExtension(String mimeType) {
        return (isImage(mimeType) || isDocument(mimeType) || isCompressed(mimeType)) ? mimeType : null;
    }

    @Override
    public String isSupported(String mimeType) {
        if (isImage(mimeType) || isDocument(mimeType) || isCompressed(mimeType)) {
            return mimeType;
        } else {
            return null;
        }
    }

    @Override
    public String isSupportedExcel(String mimeType) {
        return isExcel(mimeType) ? mimeType : null;
    }
}
