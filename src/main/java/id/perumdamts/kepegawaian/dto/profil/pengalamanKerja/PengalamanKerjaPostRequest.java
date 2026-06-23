package id.perumdamts.kepegawaian.dto.profil.pengalamanKerja;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@Data
public class PengalamanKerjaPostRequest {
    @NotEmpty(message = "Biodata ID is required")
    private String biodataId;
    @NotEmpty(message = "Nama Perusahaan is required")
    private String namaPerusahaan;
    private String typePerusahaan;
    private String jabatan;
    private String lokasi;
    private Integer tahunMasuk;
    private Integer tahunKeluar;
    private String notes;

    @JsonIgnore
    public Specification<PengalamanKerja> getSpecification() {
        return SpecificationBuilder.<PengalamanKerja>of()
                .addEqual(biodataId, "biodata", "nik")
                .addEqual(namaPerusahaan, "namaPerusahaan")
                .addEqual(typePerusahaan, "typePerusahaan")
                .addEqual(jabatan, "jabatan")
                .build();
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
