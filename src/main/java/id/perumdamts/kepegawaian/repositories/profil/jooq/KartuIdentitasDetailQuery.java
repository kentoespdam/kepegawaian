package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasDetail;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.JenisKitas.JENIS_KITAS;
import static id.perumdamts.kepegawaian.jooq.tables.KartuIdentitas.KARTU_IDENTITAS;
import static id.perumdamts.kepegawaian.jooq.tables.LampiranProfil.LAMPIRAN_PROFIL;

@Repository
@RequiredArgsConstructor
public class KartuIdentitasDetailQuery {
    private final DSLContext dsl;

    public Optional<KartuIdentitasDetail> getById(Long id) {
        return dsl.select(KartuIdentitasSelects.COLUMNS)
                .select(
                        DSL.multiset(
                                dsl.select(LAMPIRAN_PROFIL.ID, LAMPIRAN_PROFIL.FILE_NAME, LAMPIRAN_PROFIL.MIME_TYPE)
                                        .from(LAMPIRAN_PROFIL)
                                        .where(LAMPIRAN_PROFIL.REF_ID.eq(id))
                                        .and(LAMPIRAN_PROFIL.REF.eq((byte) EJenisLampiranProfil.KARTU_IDENTITAS.ordinal()))
                                        .and(LAMPIRAN_PROFIL.IS_DELETED.eq(false))
                        ).as("lampiran").convertFrom(r -> r.map(rec ->
                                new LampiranRow(
                                        rec.component1(),
                                        rec.component2(),
                                        rec.component3()
                                )))
                )
                .from(KARTU_IDENTITAS)
                .leftJoin(BIODATA).on(KARTU_IDENTITAS.NIK.eq(BIODATA.NIK))
                .leftJoin(JENIS_KITAS).on(KARTU_IDENTITAS.JENIS_KITAS_ID.eq(JENIS_KITAS.ID))
                .where(KARTU_IDENTITAS.ID.eq(id))
                .fetch(record -> {
                    KartuIdentitasDetail d = new KartuIdentitasDetail();
                    d.setId(record.get("id", Long.class));
                    d.setBiodataId(record.get("self_nik", String.class));
                    d.setBiodataNik(record.get("biodata_nik", String.class));
                    d.setBiodataNama(record.get("biodata_nama", String.class));
                    d.setJenisKartuId(record.get("jenis_kartu_id", Long.class));
                    d.setJenisKartuNama(record.get("jenis_kartu_nama", String.class));
                    d.setNomorKartu(record.get("nomor_kartu", String.class));
                    d.setTanggalExpired(record.get("tanggal_expired", java.time.LocalDate.class));
                    d.setTanggalTerima(record.get("tanggal_terima", java.time.LocalDate.class));
                    d.setNotes(record.get("notes", String.class));
                    d.setChangedStatus(record.get("changed_status", Byte.class));
                    List<LampiranRow> lampiran = record.get("lampiran", List.class);
                    d.setLampiran(lampiran != null ? lampiran : List.of());
                    return d;
                })
                .stream()
                .findFirst();
    }
}
