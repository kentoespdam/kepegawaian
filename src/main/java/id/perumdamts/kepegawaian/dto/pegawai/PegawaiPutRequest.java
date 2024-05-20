package id.perumdamts.kepegawaian.dto.pegawai;

import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PegawaiPutRequest extends PegawaiPostRequest {
    public static Pegawai toEntity(
            Pegawai entity,
            PegawaiPutRequest request,
            Biodata biodata,
            StatusPegawai statusPegawai,
            Jabatan jabatan,
            Organisasi organisasi,
            Profesi profesi,
            Golongan golongan,
            Grade grade,
            StatusKerja statusKerja
    ) {
        entity.setNipam(request.getNipam());
        entity.setBiodata(biodata);
        entity.setStatusPegawai(statusPegawai);
        entity.setJabatan(jabatan);
        entity.setOrganisasi(organisasi);
        entity.setProfesi(profesi);
        entity.setGolongan(golongan);
        entity.setGrade(grade);
        entity.setStatusKerja(statusKerja);

        return entity;
    }
}
