package id.perumdamts.kepegawaian.dto.pegawai;

import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class PegawaiPatchProfil {
    private Long id;
    private String nipam;
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
    private Long absensiId;

    public static Pegawai toEntity(Pegawai entity, PegawaiPatchProfil request, Golongan golongan) {
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

        entity.setNipam(request.getNipam());
        entity.setBiodata(biodata);
        if (Objects.nonNull(golongan))
            entity.setGolongan(golongan);
        entity.setAbsensiId(request.getAbsensiId());
        return entity;
    }
}
