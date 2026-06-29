package id.perumdamts.kepegawaian.services.kepegawaian.riwayatSp;

import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpRequest;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jooq.RiwayatSpQueryRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class RiwayatSpQueryService {
    private final RiwayatSpQueryRepository queryRepository;
    private final FileUploadUtil fileUploadUtil;

    public Page<RiwayatSpQuery> pageQuery(Long id, RiwayatSpRequest request) {
        return queryRepository.pageQuery(id, request);
    }

    public RiwayatSpQuery getById(Long id) {
        return queryRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Riwayat SP not found"));
    }

    public ResponseEntity<?> getFile(Long id) {
        RiwayatSpQueryRepository.HashedSpFileInfo fileInfo = queryRepository.getHashedFileInfoById(id)
                .orElse(null);
        if (fileInfo == null || fileInfo.hashedFileName() == null) {
            return ErrorResult.build("File not found");
        }
        try {
            Path path = fileUploadUtil.generatePathSp(String.valueOf(fileInfo.jenisSpKode()), fileInfo.hashedFileName());
            FileInputStream stream = new FileInputStream(path.toFile());
            ByteArrayResource resource = new ByteArrayResource(stream.readAllBytes());
            stream.close();
            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .header("Content-Type", fileInfo.mimeType())
                    .header("Content-Disposition", "inline; filename=\"" + fileInfo.fileName() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ErrorResult.build("File not found");
        }
    }
}
