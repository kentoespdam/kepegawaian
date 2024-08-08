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
    public UploadResultUtil uploadFile(MultipartFile file, Enum<?> ref, String subFolder) {
        String mimeType = mimeTypesUtils.isSupported(file.getContentType());
        if (mimeType == null)
            return UploadResultUtil.build(false, "File type not supported");

        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        String hashedFileName = randomStringHelper.generate();
        String originalFilename = file.getOriginalFilename();

        Path dir = createDirectory(ref, subFolder);
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
    public Path generatePath(Enum<?> ref, String subFolder, String fileName) {
        String directoryPath = BASE_PATH + ref.name() + "/" + subFolder + "/" + fileName;
        return Paths.get(directoryPath);
    }

    @Override
    public void deleteOldFile(String fileName, EJenisLampiranProfil ref, String subFolder) {
        try {
            if (fileName == null || fileName.isEmpty())
                return;
            Path filePath = Paths.get(BASE_PATH, ref.name(), subFolder, fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path generatePath(Enum<?> ref, String subFolder) {
        String directoryPath = BASE_PATH + ref.name() + "/" + subFolder;
        return Paths.get(directoryPath);
    }

    private Path createDirectory(Enum<?> ref, String subFolder) {
        try {
            Path directory = this.generatePath(ref, subFolder);
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
