package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianDetail;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianQuery;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.jooq.tables.Biodata;
import id.perumdamts.kepegawaian.jooq.tables.JenisKeahlian;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Keahlian.KEAHLIAN;
import static id.perumdamts.kepegawaian.jooq.tables.LampiranProfil.LAMPIRAN_PROFIL;

@Repository
@RequiredArgsConstructor
public class KeahlianDetailQuery {

    private final DSLContext dsl;

    public Optional<KeahlianDetail> getById(Long id) {
        return dsl.select(KeahlianSelects.COLUMNS)
                .select(DSL.multiset(
                                dsl.select(
                                                LAMPIRAN_PROFIL.ID,
                                                LAMPIRAN_PROFIL.FILE_NAME,
                                                LAMPIRAN_PROFIL.MIME_TYPE
                                        )
                                        .from(LAMPIRAN_PROFIL)
                                        .where(LAMPIRAN_PROFIL.REF_ID.eq(KEAHLIAN.ID))
                                        .and(LAMPIRAN_PROFIL.REF.eq(
                                                (byte) EJenisLampiranProfil.PROFIL_KEAHLIAN.ordinal()))
                                        .and(LAMPIRAN_PROFIL.IS_DELETED.eq(false))
                        ).as("lampiran")
                        .convertFrom(r -> r.map(rec ->
                                new LampiranRow(
                                        rec.component1(),
                                        rec.component2(),
                                        rec.component3()
                                )))
                )
                .from(KEAHLIAN)
                .leftJoin(Biodata.BIODATA)
                .on(KEAHLIAN.BIODATA_ID.eq(Biodata.BIODATA.NIK))
                .leftJoin(JenisKeahlian.JENIS_KEAHLIAN)
                .on(KEAHLIAN.JENIS_KEAHLIAN_ID.eq(JenisKeahlian.JENIS_KEAHLIAN.ID))
                .where(KEAHLIAN.ID.eq(id))
                .and(KEAHLIAN.IS_DELETED.eq(false))
                .fetch(record -> {
                    KeahlianDetail detail = new KeahlianDetail();
                    KeahlianQuery base = new KeahlianRowMapper().map(record);
                    detail.setId(base.getId());
                    detail.setBiodataId(base.getBiodataId());
                    detail.setBiodataNik(base.getBiodataNik());
                    detail.setBiodataNama(base.getBiodataNama());
                    detail.setJenisKeahlianId(base.getJenisKeahlianId());
                    detail.setJenisKeahlian(base.getJenisKeahlian());
                    detail.setKualifikasi(base.getKualifikasi());
                    detail.setSertifikasi(base.getSertifikasi());
                    detail.setInstitusi(base.getInstitusi());
                    detail.setTahun(base.getTahun());
                    detail.setMasaBerlaku(base.getMasaBerlaku());
                    detail.setDisetujui(base.getDisetujui());
                    detail.setTanggalPengajuan(base.getTanggalPengajuan());
                    detail.setTanggalDisetujui(base.getTanggalDisetujui());
                    detail.setDisetujuiOleh(base.getDisetujuiOleh());
                    detail.setChangedStatus(base.getChangedStatus());
                    detail.setLampiran(record.get("lampiran", java.util.List.class));
                    return detail;
                })
                .stream()
                .findFirst();
    }
}
