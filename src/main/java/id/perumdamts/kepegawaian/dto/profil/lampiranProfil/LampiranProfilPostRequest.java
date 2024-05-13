package id.perumdamts.kepegawaian.dto.profil.lampiranProfil;

import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public class LampiranProfilPostRequest implements Serializable {
    private EJenisLampiranProfil ref;
    @Min(value = 1, message = "Ref ID must be greater than or equal to 1")
    private Long refId;
    @NotNull(message = "File Name is required")
    private MultipartFile fileName;

    public Specification<LampiranProfil> getSpecification() {
        Specification<LampiranProfil> refSpec = (root, query, cb) ->
                cb.equal(root.get("ref"), ref);
        Specification<LampiranProfil> refIdSpec = (root, query, cb) ->
                cb.equal(root.get("refId"), refId);
        Specification<LampiranProfil> fileNameSpec = (root, query, cb) ->
                cb.equal(root.get("fileName"), fileName.getName());
        return Specification.where(refSpec).and(refIdSpec).and(fileNameSpec);
    }

    public static LampiranProfil toEntity(LampiranProfilPostRequest request, String fileName, String hashedFileName, String mimeType) {
        LampiranProfil entity = new LampiranProfil();
        entity.setRef(request.getRef());
        entity.setRefId(request.getRefId());
        entity.setFileName(fileName);
        entity.setHashedFileName(hashedFileName);
        entity.setMimeType(mimeType);
        entity.setDisetujui(false);
        entity.setTanggalPengajuan(LocalDateTime.now());
        return entity;
    }
}
