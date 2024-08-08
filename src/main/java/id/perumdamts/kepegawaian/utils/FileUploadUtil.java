package id.perumdamts.kepegawaian.utils;

import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileUploadUtil {
    String BASE_PATH = System.getProperty("user.dir") + "/attachments/";

    UploadResultUtil uploadFile(MultipartFile file, Enum<?> ref, String subFolder);

    Path generatePath(Enum<?> ref, String subFolder, String fileName);

    void deleteOldFile(String fileName, EJenisLampiranProfil ref, String subFolder);
}
