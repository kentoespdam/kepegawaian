package id.perumdamts.kepegawaian.services.profil.biodata;

import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataDetail;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataQuery;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jooq.BiodataDetailQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.BiodataQueryRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BiodataQueryService {
    private final BiodataQueryRepository queries;
    private final BiodataDetailQuery detail;
    private final BiodataRepository repository;
    private final FileUploadUtil fileUploadUtil;

    public Page<BiodataQuery> pageQuery(BiodataIndexQuery query) {
        return queries.pageQuery(query);
    }

    public BiodataDetail getById(String nik) {
        return detail.getById(nik)
                .orElseThrow(() -> new NotFoundException("Biodata not found"));
    }

    public List<BiodataQuery> findAll(BiodataIndexQuery query) {
        return queries.listQuery(query);
    }

    public ResponseEntity<?> findFotoProfil(String id) {
        Biodata biodata = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unknown Biodata"));

        if (biodata.getFotoProfil() == null || biodata.getFotoProfil().isEmpty()) {
            return ErrorResult.build("Foto Profil Not Found!");
        }

        try {
            Path path = fileUploadUtil.generatePath(EJenisLampiranProfil.FOTO_PROFIL, id, biodata.getFotoProfil());
            FileInputStream stream = new FileInputStream(path.toFile());
            String extension = FilenameUtils.getExtension(path.toFile().getName());
            ByteArrayResource resource = new ByteArrayResource(stream.readAllBytes());
            stream.close();
            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .header("Content-Type", "image/" + extension)
                    .header("Content-Disposition", "inline; filename=\"" + biodata.getFotoProfil() + "\"")
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
