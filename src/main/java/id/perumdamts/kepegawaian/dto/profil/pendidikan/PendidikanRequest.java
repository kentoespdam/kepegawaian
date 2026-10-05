package id.perumdamts.kepegawaian.dto.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@EqualsAndHashCode(callSuper = true)
@Data
public class PendidikanRequest extends PagedRequest {
    private String biodataId;
    private Long jenjangId;
    private String gelarDepan;
    private String gelarBelakang;
    private String jurusan;
    private String institusi;
    private String kota;
    private Integer tahunMasuk;
    private Integer tahunLulus;
    private Double gpa;
    private Boolean isLatest;



    @Override
    public Pageable getPageable() {
        String activeSortBy = getSortBy() == null ? "JenjangPendidikan.Id" : getSortBy();
        Sort.Direction activeDirection = getSortDirection() == null || getSortDirection().equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(getPageNumber(), getSizeOrDefault(), Sort.by(activeDirection, activeSortBy));
    }
}
