package id.perumdamts.kepegawaian.services.kepegawaian.lampiran;

import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkQuery;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jooq.LampiranSkQueryRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LampiranSkQueryService {
    private final LampiranSkQueryRepository queryRepository;
    private final FileUploadUtil fileUploadUtil;

    public List<LampiranSkQuery> getLampiran(EJenisSk jenisSk, Long refId) {
        return queryRepository.findByRefAndRefId(jenisSk, refId);
    }

    public LampiranSkQuery getLampiranById(Long id) {
        return queryRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Lampiran SK not found"));
    }

    public ResponseEntity<?> getFileLampiranById(EJenisSk jenisSk, Long id) {
        LampiranSkQueryRepository.HashedFileInfo fileInfo = queryRepository.getHashedFileInfoById(id)
                .orElse(null);
        if (fileInfo == null || fileInfo.hashedFileName() == null) {
            return ErrorResult.build("File Not Found");
        }
        try {
            Path path = fileUploadUtil.generatePath(jenisSk, String.valueOf(fileInfo.refId()), fileInfo.hashedFileName());
            FileInputStream stream = new FileInputStream(path.toFile());
            ByteArrayResource resource = new ByteArrayResource(stream.readAllBytes());
            stream.close();
            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .header("Content-Type", fileInfo.mimeType())
                    .header("Content-Disposition", "inline; filename=\"" + fileInfo.fileName() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ErrorResult.build("File Not Found");
        }
    }
}
