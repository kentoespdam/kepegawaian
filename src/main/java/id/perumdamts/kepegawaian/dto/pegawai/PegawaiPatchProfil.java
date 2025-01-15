package id.perumdamts.kepegawaian.dto.pegawai;

import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class PegawaiPatchProfil {
    @NotNull(message = "ID is required")
    @Min(value = 1, message = "ID is required")
    private Long id;
    @NotEmpty(message = "NIPAM is required")
    @NotNull(message = "NIPAM is required")
    private String nipam;
    @NotEmpty(message = "Nama is required")
    @NotNull(message = "Nama is required")
    private String nama;
    private EJenisKelamin jenisKelamin;
    private EStatusKawin statusKawin;
    private EAgama agama;
    private String tempatLahir;
    private LocalDate tanggalLahir;
    private String alamat;
    private String ibuKandung;
    private String telp;
    private Long golonganId;
    private Long organisasiId;
    private Long jabatanId;
    private Long profesiId;
    private Long absensiId;

    public static Pegawai toEntity(Pegawai entity, PegawaiPatchProfil request, Golongan golongan, Organisasi organisasi, Jabatan jabatan, Profesi profesi) {
        Biodata biodata = getBiodata(entity, request);

        entity.setNipam(request.getNipam());
        entity.setBiodata(biodata);
        if (Objects.nonNull(golongan))
            entity.setGolongan(golongan);
        entity.setOrganisasi(organisasi);
        entity.setJabatan(jabatan);
        if (Objects.nonNull(profesi))
            entity.setProfesi(profesi);
        entity.setAbsensiId(request.getAbsensiId());
        return entity;
    }

    private static Biodata getBiodata(Pegawai entity, PegawaiPatchProfil request) {
        Biodata biodata = entity.getBiodata();
        biodata.setNama(request.getNama());
        biodata.setJenisKelamin(request.getJenisKelamin());
        biodata.setStatusKawin(request.getStatusKawin());
        biodata.setAgama(request.getAgama());
        biodata.setTempatLahir(request.getTempatLahir());
        biodata.setTanggalLahir(request.getTanggalLahir());
        biodata.setAlamat(request.getAlamat());
        biodata.setIbuKandung(request.getIbuKandung());
        biodata.setTelp(request.getTelp());
        return biodata;
    }
}
