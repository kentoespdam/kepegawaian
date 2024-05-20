package id.perumdamts.kepegawaian.dto.profil.biodata;

import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EGolonganDarah;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@Data
public class BiodataPostRequest {
    @NotEmpty(message = "NIK is required")
    private String nik;
    @NotEmpty(message = "Nama is required")
    private String nama;
    @NotEmpty(message = "Tempat Lahir is required")
    private String tempatLahir;
    @NotNull(message = "Tanggal Lahir is required")
    private LocalDate tanggalLahir;
    @NotEmpty(message = "Alamat is required")
    private String alamat;
    private String telp;
    @NotNull(message = "Agama is required")
    @Enumerated(value = EnumType.ORDINAL)
    private EAgama agama;
    @NotEmpty(message = "Ibu Kandung is required")
    private String ibuKandung;
    @Min(value = 1L, message = "Pendidikan Terakhir is required")
    private Long pendidikanTerakhir;
    @Enumerated(value = EnumType.STRING)
    private EGolonganDarah golonganDarah;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKawin statusKawin;
    private String notes;

    public Specification<Biodata> getSpecification() {
        Specification<Biodata> nikSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("nik"), nik);
        Specification<Biodata> namaSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("nama"), nama);
        Specification<Biodata> tempatLahirSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tempatLahir"), tempatLahir);
        Specification<Biodata> tanggalLahirSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tanggalLahir"), tanggalLahir);

        return Specification.where(nikSpec).and(namaSpec).and(tempatLahirSpec)
                .and(tanggalLahirSpec);
    }

    public static Biodata toEntity(BiodataPostRequest request, JenjangPendidikan pendidikanTerakhir) {
        Biodata entity = new Biodata();
        entity.setNik(request.getNik());
        entity.setNama(request.getNama());
        entity.setTempatLahir(request.getTempatLahir());
        entity.setTanggalLahir(request.getTanggalLahir());
        entity.setAlamat(request.getAlamat());
        entity.setTelp(request.getTelp());
        entity.setAgama(request.getAgama());
        entity.setIbuKandung(request.getIbuKandung());
        entity.setPendidikanTerakhir(pendidikanTerakhir);
        entity.setGolonganDarah(request.getGolonganDarah());
        entity.setStatusKawin(request.getStatusKawin());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
