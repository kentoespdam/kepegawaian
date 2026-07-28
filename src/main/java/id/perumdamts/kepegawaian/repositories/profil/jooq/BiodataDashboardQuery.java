package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataDashboardResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataDashboardResponse.PendidikanDashboard;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.GajiPendapatanNonPajak.GAJI_PENDAPATAN_NON_PAJAK;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Pendidikan.PENDIDIKAN;

@Repository
@RequiredArgsConstructor
public class BiodataDashboardQuery {
    private final DSLContext dsl;

    public Optional<BiodataDashboardResponse> getByNik(String nik) {
        return dsl.select(
                        BIODATA.NIK,
                        BIODATA.NAMA,
                        BIODATA.JENIS_KELAMIN,
                        BIODATA.TEMPAT_LAHIR,
                        BIODATA.TANGGAL_LAHIR,
                        BIODATA.AGAMA,
                        BIODATA.STATUS_KAWIN,
                        BIODATA.ALAMAT,
                        BIODATA.TELP,
                        PEGAWAI.EMAIL,
                        GAJI_PENDAPATAN_NON_PAJAK.KODE,
                        BIODATA.IBU_KANDUNG,
                        JENJANG_PENDIDIKAN.NAMA.as("tingkat"),
                        PENDIDIKAN.JURUSAN,
                        PENDIDIKAN.INSTITUSI,
                        PENDIDIKAN.TAHUN_LULUS,
                        BIODATA.field("changed_status", Boolean.class)
                ).from(BIODATA)
                .join(PEGAWAI).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(GAJI_PENDAPATAN_NON_PAJAK).on(PEGAWAI.GAJI_PENDAPATAN_NON_PAJAK_ID.eq(GAJI_PENDAPATAN_NON_PAJAK.ID)
                        .and(GAJI_PENDAPATAN_NON_PAJAK.IS_DELETED.eq(false)))
                .leftJoin(PENDIDIKAN).on(PENDIDIKAN.BIODATA_ID.eq(BIODATA.NIK)
                        .and(PENDIDIKAN.IS_LATEST.eq((byte) 1))
                        .and(PENDIDIKAN.CHANGED_STATUS.eq((byte) 0))
                        .and(PENDIDIKAN.IS_DELETED.eq(false)))
                .leftJoin(JENJANG_PENDIDIKAN).on(PENDIDIKAN.JENJANG_ID.eq(JENJANG_PENDIDIKAN.ID)
                        .and(JENJANG_PENDIDIKAN.IS_DELETED.eq(false)))
                .where(BIODATA.NIK.eq(nik))
                .and(BIODATA.IS_DELETED.eq(false))
                .fetchOptional()
                .map(BiodataDashboardQuery::mapRow);
    }

    static BiodataDashboardResponse mapRow(Record r) {
        String jenisKelamin = null;
        Byte jkByte = r.get(BIODATA.JENIS_KELAMIN);
        if (jkByte != null) {
            jenisKelamin = EJenisKelamin.values()[jkByte] == EJenisKelamin.LAKI_LAKI
                    ? "Laki-Laki" : "Perempuan";
        }

        String agama = null;
        Byte agByte = r.get(BIODATA.AGAMA);
        if (agByte != null) {
            agama = EAgama.values()[agByte].toString();
        }

        String statusKawin = null;
        Byte skByte = r.get(BIODATA.STATUS_KAWIN);
        if (skByte != null) {
            statusKawin = EStatusKawin.values()[skByte].toString();
        }

        String kodePajak = r.get(GAJI_PENDAPATAN_NON_PAJAK.KODE);
        String email = r.get(PEGAWAI.EMAIL);
        String noTelp = r.get(BIODATA.TELP);
        LocalDate tanggalLahir = r.get(BIODATA.TANGGAL_LAHIR);
        String tempatLahir = r.get(BIODATA.TEMPAT_LAHIR);
        String alamat = r.get(BIODATA.ALAMAT);

        String tingkat = r.get("tingkat", String.class);
        String jurusan = r.get(PENDIDIKAN.JURUSAN);
        String institusi = r.get(PENDIDIKAN.INSTITUSI);
        Integer tahunLulus = r.get(PENDIDIKAN.TAHUN_LULUS);

        PendidikanDashboard pendidikan = (tingkat != null || jurusan != null
                || institusi != null || tahunLulus != null)
                ? new PendidikanDashboard(tingkat, jurusan, institusi, tahunLulus)
                : null;

        Boolean changedStatus = r.get("changed_status", Boolean.class);

        return new BiodataDashboardResponse(
                r.get(BIODATA.NIK),
                r.get(BIODATA.NAMA),
                jenisKelamin,
                tempatLahir,
                tanggalLahir,
                agama,
                statusKawin,
                alamat,
                noTelp,
                email,
                kodePajak,
                r.get(BIODATA.IBU_KANDUNG),
                pendidikan,
                changedStatus
        );
    }
}
