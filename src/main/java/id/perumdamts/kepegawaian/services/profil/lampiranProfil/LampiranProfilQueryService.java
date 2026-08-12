package id.perumdamts.kepegawaian.services.profil.lampiranProfil;

import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilQuery;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.services.profil.OwnershipGuard;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import id.perumdamts.kepegawaian.repositories.profil.jooq.LampiranProfilQueryRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.LampiranProfilRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LampiranProfilQueryService {
    private final LampiranProfilQueryRepository queryRepo;
    private final LampiranProfilRepository jpaRepository;
    private final FileUploadUtil fileUploadUtil;
    private final OwnershipGuard ownershipGuard;

    public List<LampiranProfilQuery> getLampiran(EJenisLampiranProfil jenis, Long id) {
        ownershipGuard.assertSelfReadLampiran(jenis, id);
        return queryRepo.findByRefAndRefId(jenis, id);
    }

    public LampiranProfilQuery getLampiranById(Long id) {
        LampiranProfilQuery result = queryRepo.getById(id).orElse(null);
        if (result != null) {
            ownershipGuard.assertSelfReadLampiran(result.ref(), result.refId());
        }
        return result;
    }

    public ResponseEntity<?> getFileLampiranById(EJenisLampiranProfil jenis, Long id) {
        LampiranProfil lampiranProfil = jpaRepository.findById(id).orElse(null);
        if (Objects.isNull(lampiranProfil))
            return ErrorResult.build("File Not Found!");
        ownershipGuard.assertSelfReadLampiran(lampiranProfil.getRef(), lampiranProfil.getRefId());
        try {
            Path path = fileUploadUtil.generatePath(
                    jenis, String.valueOf(lampiranProfil.getRefId()),
                    lampiranProfil.getHashedFileName());
            FileInputStream stream = new FileInputStream(path.toFile());
            ByteArrayResource resource = new ByteArrayResource(stream.readAllBytes());
            stream.close();
            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .header("Content-Type", lampiranProfil.getMimeType())
                    .header("Content-Disposition",
                            "inline; filename=\"" + lampiranProfil.getFileName() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ErrorResult.build("File Not Found!");
        }
    }
}
