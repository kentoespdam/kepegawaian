package id.perumdamts.kepegawaian.utils;

import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileUploadUtil {
    String BASE_PATH = System.getProperty("user.dir") + "/attachments/";

    UploadResultUtil uploadFile(MultipartFile file, EJenisLampiranProfil ref, String fileName);

    Path generatePath(EJenisLampiranProfil ref, String username);

    Path generatePath(EJenisLampiranProfil ref, String username, String fileName);

    void deleteOldFile(String oldFileName);

    void deleteOldFile(String fileName, EJenisLampiranProfil ref, String username);
}
