package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanDetail;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.JenisPelatihan.JENIS_PELATIHAN;
import static id.perumdamts.kepegawaian.jooq.tables.LampiranProfil.LAMPIRAN_PROFIL;
import static id.perumdamts.kepegawaian.jooq.tables.Pelatihan.PELATIHAN;

@Repository
@RequiredArgsConstructor
public class PelatihanDetailQuery {
    private final DSLContext dsl;

    @SuppressWarnings("unchecked")
    public Optional<PelatihanDetail> getById(Long id) {
        var result = dsl.select(
                        PELATIHAN.ID,
                        PELATIHAN.BIODATA_ID,
                        PELATIHAN.JENIS_PELATIHAN_ID,
                        PELATIHAN.NAMA,
                        PELATIHAN.LEMBAGA,
                        PELATIHAN.TANGGAL_MULAI,
                        PELATIHAN.TANGGAL_SELESAI,
                        PELATIHAN.LULUS,
                        PELATIHAN.NILAI,
                        PELATIHAN.IKATAN_DINAS,
                        PELATIHAN.TANGGAL_AKHIR_IKATAN,
                        PELATIHAN.NOTES,
                        PELATIHAN.CHANGED_STATUS,
                        BIODATA.NIK.as("biodata_nik"),
                        BIODATA.NAMA.as("biodata_nama"),
                        JENIS_PELATIHAN.NAMA.as("jenis_pelatihan_nama"),
                        DSL.multiset(
                                DSL.select(LAMPIRAN_PROFIL.ID, LAMPIRAN_PROFIL.FILE_NAME, LAMPIRAN_PROFIL.MIME_TYPE)
                                        .from(LAMPIRAN_PROFIL)
                                        .where(LAMPIRAN_PROFIL.REF.eq((byte) EJenisLampiranProfil.PROFIL_PELATIHAN.ordinal())
                                                .and(LAMPIRAN_PROFIL.REF_ID.eq(PELATIHAN.ID))
                                                .and(LAMPIRAN_PROFIL.IS_DELETED.eq(false)))
                        ).as("lampiran"))
                .from(PELATIHAN)
                .leftJoin(BIODATA).on(PELATIHAN.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JENIS_PELATIHAN).on(PELATIHAN.JENIS_PELATIHAN_ID.eq(JENIS_PELATIHAN.ID))
                .where(PELATIHAN.ID.eq(id))
                .fetch();

        return result.stream().findFirst().map(r -> {
            PelatihanDetail d = new PelatihanDetail();
            d.setId(r.get(PELATIHAN.ID));
            d.setBiodataId(r.get(PELATIHAN.BIODATA_ID));
            d.setBiodataNik(r.get("biodata_nik", String.class));
            d.setBiodataNama(r.get("biodata_nama", String.class));
            d.setJenisPelatihanId(r.get(PELATIHAN.JENIS_PELATIHAN_ID));
            d.setJenisPelatihanNama(r.get("jenis_pelatihan_nama", String.class));
            d.setNama(r.get(PELATIHAN.NAMA));
            d.setLembaga(r.get(PELATIHAN.LEMBAGA));
            d.setTanggalMulai(r.get(PELATIHAN.TANGGAL_MULAI));
            d.setTanggalSelesai(r.get(PELATIHAN.TANGGAL_SELESAI));
            d.setLulus(r.get(PELATIHAN.LULUS));
            d.setNilai(r.get(PELATIHAN.NILAI));
            d.setIkatanDinas(r.get(PELATIHAN.IKATAN_DINAS));
            d.setTanggalAkhirIkatan(r.get(PELATIHAN.TANGGAL_AKHIR_IKATAN));
            d.setNotes(r.get(PELATIHAN.NOTES));
            d.setChangedStatus(r.get(PELATIHAN.CHANGED_STATUS));
            var lampiranRows = r.get("lampiran", java.util.List.class);
            if (lampiranRows != null) {
                d.setLampiran(((java.util.List<java.util.List<Object>>) lampiranRows).stream()
                        .map(row -> new LampiranRow(
                                ((Number) row.get(0)).longValue(),
                                (String) row.get(1),
                                (String) row.get(2)))
                        .toList());
            }
            return d;
        });
    }
}