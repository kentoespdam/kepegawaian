package id.perumdamts.kepegawaian.utils;

import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RequiredArgsConstructor
@Service
public class FileUploadUtilImpl implements FileUploadUtil {
    private final RandomStringHelper randomStringHelper;
    private final MimeTypesUtils mimeTypesUtils;

    @Override
    public UploadResultUtil uploadFile(MultipartFile file, EJenisLampiranProfil ref, String username) {
        String mimeType = mimeTypesUtils.isSupported(file.getContentType());
        if (mimeType == null)
            return UploadResultUtil.build(false, "File type not supported");

        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        String hashedFileName = randomStringHelper.generate();
        String originalFilename = file.getOriginalFilename();

        Path dir = createDirectory(ref, username);

        boolean saved = ref == EJenisLampiranProfil.FOTO_PROFIL ?
                saveToStorage(file, dir, originalFilename) :
                saveToStorage(file, dir, hashedFileName);
        if (!saved)
            return UploadResultUtil.build(false, "Failed to save file");

        return UploadResultUtil.build(
                true,
                "File uploaded successfully",
                originalFilename,
                fileExtension,
                mimeType,
                hashedFileName
        );
    }

    @Override
    public Path generatePath(EJenisLampiranProfil ref, String username) {
        String directoryPath = BASE_PATH + ref.name() + "/" + username;
        return Paths.get(directoryPath);
    }

    @Override
    public Path generatePath(EJenisLampiranProfil ref, String username, String fileName) {
        String directoryPath = BASE_PATH + ref.name() + "/" + username + "/" + fileName;
        return Paths.get(directoryPath);
    }

    @Override
    public void deleteOldFile(String oldFileName) {
        try {
            Path filePath = Paths.get(BASE_PATH, oldFileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteOldFile(String fileName, EJenisLampiranProfil ref, String username) {
        try {
            if (fileName == null || fileName.isEmpty())
                return;
            Path filePath = Paths.get(BASE_PATH, ref.name(), username, fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path createDirectory(EJenisLampiranProfil ref, String username) {
        try {
            Path directory = this.generatePath(ref, username);
            Files.createDirectories(directory);
            return directory;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean saveToStorage(MultipartFile file, Path dir, String hashedFileName) {
        try {
            Files.copy(
                    file.getInputStream(),
                    dir.resolve(hashedFileName),
                    StandardCopyOption.REPLACE_EXISTING
            );
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
