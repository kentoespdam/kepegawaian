package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaDetail;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaQuery;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.mapper.profil.keluarga.ProfilKeluargaJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.LampiranProfil.LAMPIRAN_PROFIL;
import static id.perumdamts.kepegawaian.jooq.tables.ProfilKeluarga.PROFIL_KELUARGA;
import static org.jooq.Records.mapping;

@Repository
@RequiredArgsConstructor
public class ProfilKeluargaDetailQuery {
    private final DSLContext dsl;

    public Optional<ProfilKeluargaDetail> getById(Long id) {
        return dsl.select(ProfilKeluargaSelects.COLUMNS)
                .select(
                        DSL.multiset(
                                dsl.select(LAMPIRAN_PROFIL.ID, LAMPIRAN_PROFIL.FILE_NAME, LAMPIRAN_PROFIL.MIME_TYPE)
                                        .from(LAMPIRAN_PROFIL)
                                        .where(LAMPIRAN_PROFIL.REF_ID.eq(id))
                                        .and(LAMPIRAN_PROFIL.REF.eq((byte) EJenisLampiranProfil.PROFIL_KELUARGA.ordinal()))
                                        .and(LAMPIRAN_PROFIL.IS_DELETED.eq(false))
                        ).as("lampiran").convertFrom(r -> r.map(mapping(LampiranRow::new)))
                )
                .from(PROFIL_KELUARGA)
                .leftJoin(BIODATA).on(PROFIL_KELUARGA.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JENJANG_PENDIDIKAN).on(PROFIL_KELUARGA.PENDIDIKAN_ID.eq(JENJANG_PENDIDIKAN.ID))
                .where(PROFIL_KELUARGA.ID.eq(id))
                .fetch(record -> {
                    ProfilKeluargaDetail detail = new ProfilKeluargaDetail();
                    ProfilKeluargaQuery base = ProfilKeluargaJooqMapper.INSTANCE.map(record);
                    detail.setId(base.getId());
                    detail.setBiodataId(base.getBiodataId());
                    detail.setBiodataNik(base.getBiodataNik());
                    detail.setBiodataNama(base.getBiodataNama());
                    detail.setNik(base.getNik());
                    detail.setNama(base.getNama());
                    detail.setJenisKelamin(base.getJenisKelamin());
                    detail.setAgama(base.getAgama());
                    detail.setHubunganKeluarga(base.getHubunganKeluarga());
                    detail.setTempatLahir(base.getTempatLahir());
                    detail.setTanggalLahir(base.getTanggalLahir());
                    detail.setTanggungan(base.getTanggungan());
                    detail.setPendidikanId(base.getPendidikanId());
                    detail.setJenjangPendidikan(base.getJenjangPendidikan());
                    detail.setStatusPendidikan(base.getStatusPendidikan());
                    detail.setStatusKawin(base.getStatusKawin());
                    detail.setNotes(base.getNotes());
                    detail.setVersion(base.getVersion());
                    detail.setIsDeleted(base.getIsDeleted());
                    detail.setChangedStatus(base.getChangedStatus());
                    detail.setLampiran(record.get("lampiran", java.util.List.class));
                    return detail;
                })
                .stream()
                .findFirst();
    }
}