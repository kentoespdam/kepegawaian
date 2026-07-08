package id.perumdamts.kepegawaian.dto.profil.keluarga;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanMiniResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EHubunganKeluarga;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPendidikan;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;

import java.time.LocalDate;

public record ProfilKeluargaResponse(
        Long id,
        BiodataMiniResponse biodata,
        String nik,
        String nama,
        EJenisKelamin jenisKelamin,
        EAgama agama,
        EHubunganKeluarga hubunganKeluarga,
        String tempatLahir,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalLahir,
        Boolean tanggungan,
        JenjangPendidikanMiniResponse pendidikan,
        EStatusPendidikan statusPendidikan,
        Boolean statusKawin,
        String notes,
        Boolean changedStatus
) {
    public static ProfilKeluargaResponse from(ProfilKeluarga entity) {
        return new ProfilKeluargaResponse(
                entity.getId(),
                BiodataMiniResponse.from(entity.getBiodata()),
                entity.getNik(),
                entity.getNama(),
                entity.getJenisKelamin(),
                entity.getAgama(),
                entity.getHubunganKeluarga(),
                entity.getTempatLahir(),
                entity.getTanggalLahir(),
                entity.getTanggungan(),
                entity.getPendidikan() != null ? JenjangPendidikanMiniResponse.from(entity.getPendidikan()) : null,
                entity.getStatusPendidikan(),
                entity.getStatusKawin(),
                entity.getNotes(),
                entity.getChangedStatus()
        );
    }
}
