package id.perumdamts.kepegawaian.mapper.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponseDetail;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataResponse;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import org.jooq.Record;

import java.time.LocalDate;
import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

public final class PegawaiDetailRecordMapper {
    private PegawaiDetailRecordMapper() {}

    /**
     * Map a detail Record (from {@code PegawaiDetailSelects.detailFields()} +
     * kartuIdentitasMultiset) to a {@code PegawaiResponseDetail}.
     *
     * @param r      the JOOQ Record containing all selected fields
     * @param skList pre-fetched RiwayatSkResponse list (passed as argument, not fetched here)
     * @return fully populated PegawaiResponseDetail
     */
    public static PegawaiResponseDetail mapDetail(Record r, List<RiwayatSkResponse> skList) {
        // ── Scalar fields ──
        Long pegId = r.get(PEGAWAI.ID);
        String nipam = r.get(PEGAWAI.NIPAM);

        Byte statusPegawaiByte = r.get(PEGAWAI.STATUS_PEGAWAI);
        EStatusPegawai statusPegawai = statusPegawaiByte != null
                ? EStatusPegawai.values()[statusPegawaiByte] : null;

        Byte statusKerjaByte = r.get(PEGAWAI.STATUS_KERJA);
        EStatusKerja statusKerja = statusKerjaByte != null
                ? EStatusKerja.values()[statusKerjaByte] : null;

        LocalDate tmtKerja = r.get(PEGAWAI.TMT_KERJA);
        LocalDate tmtPensiun = r.get(PEGAWAI.TMT_PENSIUN);
        Double gajiPokok = r.get(PEGAWAI.GAJI_POKOK);
        Double phdp = r.get(PEGAWAI.PHDP);
        Integer jmlTanggungan = r.get(PEGAWAI.JML_TANGGUNGAN);
        Integer mkgTahun = r.get(PEGAWAI.MKG_TAHUN);
        Integer mkgBulan = r.get(PEGAWAI.MKG_BULAN);
        Long absensiId = r.get(PEGAWAI.ABSENSI_ID);
        String email = r.get(PEGAWAI.EMAIL);
        String notes = r.get(PEGAWAI.NOTES);
        Boolean isAskes = r.get(PEGAWAI.IS_ASKES);
        LocalDate tmtPegawai = r.get(PEGAWAI.TMT_PEGAWAI);

        // ── Build nested objects via RefMapper ──
        BiodataResponse biodata = PegawaiDetailRefMapper.buildBiodata(r);

        // Re-attach kartu identitas list (built from multiset)
        @SuppressWarnings("unchecked")
        var cards = (org.jooq.Result<org.jooq.Record>) r.get("kartu_identitas");
        List<KartuIdentitasMiniResponse> kartuIdentitasList = PegawaiDetailRefMapper.mapKartuIdentitasList(cards);
        if (biodata != null) {
            // Re-create biodata with kartuIdentitasList set
            biodata = new BiodataResponse(
                    biodata.nik(),
                    biodata.nama(),
                    biodata.jenisKelamin(),
                    biodata.tempatLahir(),
                    biodata.tanggalLahir(),
                    biodata.alamat(),
                    biodata.telp(),
                    biodata.agama(),
                    biodata.ibuKandung(),
                    biodata.pendidikanTerakhir(),
                    biodata.golonganDarah(),
                    biodata.statusKawin(),
                    biodata.fotoProfil(),
                    biodata.notes(),
                    kartuIdentitasList
            );
        }

        var organisasi = PegawaiDetailRefMapper.buildOrganisasi(r);
        var jabatan = PegawaiDetailRefMapper.buildJabatan(r);
        var profesi = PegawaiDetailRefMapper.buildProfesi(r);
        var golongan = PegawaiDetailRefMapper.buildGolongan(r);
        var grade = PegawaiDetailRefMapper.buildGrade(r);
        var kodePajak = PegawaiDetailRefMapper.buildKodePajak(r);
        var gajiProfil = PegawaiDetailRefMapper.buildGajiProfil(r);
        var rumahDinas = PegawaiDetailRefMapper.buildRumahDinas(r);

        // ── Compute SKs from pre-fetched list ──
        RiwayatSkResponse skCapeg = getLastByJenis(skList, EJenisSk.SK_CAPEG);
        RiwayatSkResponse skPegawai = getLastByJenis(skList, EJenisSk.SK_PEGAWAI_TETAP);
        RiwayatSkResponse skGolongan = getLastByJenis(skList, EJenisSk.SK_KENAIKAN_PANGKAT_GOLONGAN);
        RiwayatSkResponse skJabatan = getLastByJenis(skList, EJenisSk.SK_JABATAN);
        RiwayatSkResponse skMutasi = getLastByJenis(skList, EJenisSk.SK_MUTASI);
        RiwayatSkResponse skKontrak = getLastByJenis(skList, EJenisSk.SK_LAINNYA);
        RiwayatSkResponse skGajiBerkala = getLastByJenis(skList, EJenisSk.SK_KENAIKAN_GAJI_BERKALA);

        LocalDate tanggalSk = tmtPegawai;
        if (skCapeg != null) {
            tanggalSk = skCapeg.tmtBerlaku();
        }

        return new PegawaiResponseDetail(
                pegId, nipam, biodata, statusPegawai,
                organisasi, jabatan, profesi, golongan, grade,
                statusKerja, tmtKerja, skCapeg, tmtPensiun,
                skPegawai, skGolongan, skJabatan, skMutasi,
                skKontrak, skGajiBerkala,
                gajiPokok, phdp, jmlTanggungan,
                mkgTahun, mkgBulan, absensiId,
                tanggalSk, null, isAskes,
                kodePajak, gajiProfil, rumahDinas,
                email, notes
        );
    }

    private static RiwayatSkResponse getLastByJenis(List<RiwayatSkResponse> list, EJenisSk jenisSk) {
        if (list == null) return null;
        return list.stream()
                .filter(sk -> sk.jenisSk() == jenisSk)
                .findFirst()
                .orElse(null);
    }
}
