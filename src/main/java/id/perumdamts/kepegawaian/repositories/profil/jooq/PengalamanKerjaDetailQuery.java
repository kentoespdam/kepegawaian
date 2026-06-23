package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaDetail;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.LampiranProfil.LAMPIRAN_PROFIL;
import static id.perumdamts.kepegawaian.jooq.tables.PengalamanKerja.PENGALAMAN_KERJA;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multiset;

@Repository
@RequiredArgsConstructor
public class PengalamanKerjaDetailQuery {
    private final DSLContext dsl;

    public Optional<PengalamanKerjaDetail> getById(Long id) {
        return dsl.select(PengalamanKerjaSelects.COLUMNS)
                .select(multiset(dsl.select(LAMPIRAN_PROFIL.ID, LAMPIRAN_PROFIL.FILE_NAME, LAMPIRAN_PROFIL.MIME_TYPE)
                                .from(LAMPIRAN_PROFIL)
                                .where(LAMPIRAN_PROFIL.REF_ID.eq(id))
                                .and(LAMPIRAN_PROFIL.REF.eq((byte) EJenisLampiranProfil.PROFIL_PENGALAMAN_KERJA.ordinal()))
                                .and(LAMPIRAN_PROFIL.IS_DELETED.eq(false)))
                        .as("lampiran")
                        .convertFrom(r -> r.map(mapping(LampiranRow::new))))
                .from(PENGALAMAN_KERJA)
                .leftJoin(BIODATA).on(PENGALAMAN_KERJA.BIODATA_ID.eq(BIODATA.NIK))
                .where(PENGALAMAN_KERJA.ID.eq(id))
                .and(PENGALAMAN_KERJA.IS_DELETED.eq(false))
                .fetchOptional(record -> toDetail(record.intoMap()));
    }

    @SuppressWarnings("unchecked")
    private PengalamanKerjaDetail toDetail(Map<String, Object> map) {
        var detail = new PengalamanKerjaDetail();
        detail.setId((Long) map.get("id"));
        detail.setBiodataId((String) map.get("biodata_id"));
        detail.setBiodataNik((String) map.get("biodata_nik"));
        detail.setBiodataNama((String) map.get("biodata_nama"));
        detail.setNamaPerusahaan((String) map.get("nama_perusahaan"));
        detail.setTypePerusahaan((String) map.get("type_perusahaan"));
        detail.setJabatan((String) map.get("jabatan"));
        detail.setLokasi((String) map.get("lokasi"));
        detail.setTahunMasuk((Integer) map.get("tahun_masuk"));
        detail.setTahunKeluar((Integer) map.get("tahun_keluar"));
        detail.setNotes((String) map.get("notes"));
        detail.setChangedStatus((Byte) map.get("changed_status"));
        detail.setLampiran((java.util.List<LampiranRow>) map.get("lampiran"));
        return detail;
    }
}
