package id.perumdamts.kepegawaian.dto.kepegawaian.lampiran;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.LampiranSk;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class LampiranSkPostRequest implements Serializable {
    @Enumerated(EnumType.ORDINAL)
    private EJenisSk ref;
    @Min(value = 1, message = "Ref ID must be greater than or equal to 1")
    private Long refId;
    @NotNull(message = "File Name is required")
    private MultipartFile fileName;
    private String notes;

    @JsonIgnore
    public Specification<LampiranSk> getSpecification() {
        Specification<LampiranSk> refSpec = (root, query, cb) ->
                cb.equal(root.get("ref"), ref);
        Specification<LampiranSk> refIdSpec = (root, query, cb) ->
                cb.equal(root.get("refId"), refId);
        Specification<LampiranSk> fileNameSpec = (root, query, cb) ->
                cb.equal(root.get("fileName"), fileName.getName());

        return Specification.where(refSpec).and(refIdSpec).and(fileNameSpec);
    }

    public static LampiranSk toEntity(LampiranSkPostRequest request, String fileName, String hashedFileName, String mimeType) {
        LampiranSk entity = new LampiranSk();
        entity.setRef(request.getRef());
        entity.setRefId(request.getRefId());
        entity.setFileName(fileName);
        entity.setNotes(request.getNotes());
        entity.setHashedFileName(hashedFileName);
        entity.setMimeType(mimeType);
        entity.setDisetujui(false);
        entity.setTanggalPengajuan(LocalDateTime.now());
        return entity;
    }
}
