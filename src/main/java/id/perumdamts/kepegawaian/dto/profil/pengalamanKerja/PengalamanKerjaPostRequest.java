package id.perumdamts.kepegawaian.dto.profil.pengalamanKerja;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class PengalamanKerjaPostRequest {
    @NotEmpty(message = "Biodata ID is required")
    private String biodataId;
    @NotEmpty(message = "Nama Perusahaan is required")
    private String namaPerusahaan;
    @NotEmpty(message = "Type Perusahaan is required")
    private String typePerusahaan;
    @NotEmpty(message = "Jabatan is required")
    private String jabatan;
    @NotEmpty(message = "Lokasi is required")
    private String lokasi;
    private Integer tahunMasuk;
    private Integer tahunKeluar;
    private String notes;

    @JsonIgnore
    public Specification<PengalamanKerja> getSpecification() {
        Specification<PengalamanKerja> biodataSpec = Objects.isNull(biodataId) ? null :
                (root, query, cb) -> cb.equal(root.get("biodata").get("nik"), biodataId);
        Specification<PengalamanKerja> namaPerusahaanSpec = Objects.isNull(namaPerusahaan) ? null :
                (root, query, cb) -> cb.equal(root.get("namaPerusahaan"), namaPerusahaan);
        Specification<PengalamanKerja> typePerusahaanSpec = Objects.isNull(typePerusahaan) ? null :
                (root, query, cb) -> cb.equal(root.get("typePerusahaan"), typePerusahaan);
        Specification<PengalamanKerja> jabatanSpec = Objects.isNull(jabatan) ? null :
                (root, query, cb) -> cb.equal(root.get("jabatan"), jabatan);
        return Specification.where(biodataSpec).and(namaPerusahaanSpec)
                .and(typePerusahaanSpec).and(jabatanSpec);
    }

    public static PengalamanKerja toEntity(PengalamanKerjaPostRequest request, Biodata biodata) {
        PengalamanKerja entity = new PengalamanKerja();
        entity.setBiodata(biodata);
        entity.setNamaPerusahaan(request.getNamaPerusahaan());
        entity.setTypePerusahaan(request.getTypePerusahaan());
        entity.setJabatan(request.getJabatan());
        entity.setLokasi(request.getLokasi());
        entity.setTahunMasuk(request.getTahunMasuk());
        entity.setTahunKeluar(request.getTahunKeluar());
        entity.setNotes(request.getNotes());
        entity.setDisetujui(true);
        entity.setTanggalPengajuan(LocalDateTime.now());
        return entity;
    }
}
