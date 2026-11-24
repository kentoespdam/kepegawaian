package id.perumdamts.kepegawaian.repositories.laporan.kepegawaian;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.SoResponse;
import id.perumdamts.kepegawaian.mapper.laporan.kepegawaian.SoRecordMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SoRepository {
    private final DSLContext dsl;

    public List<SoResponse> fetch(Integer maxLevel) {
        Condition condition = JABATAN.IS_DELETED.eq(false);
        if (maxLevel != null && maxLevel > 0) {
            condition = condition.and(JABATAN.LEVEL_ID.le((long) maxLevel));
        }

        return dsl.select(
                        JABATAN.ID.as("id"),
                        JABATAN.PARENT_ID.as("parent_id"),
                        JABATAN.KODE.as("kode_jabatan"),
                        JABATAN.NAMA.as("nama_jabatan"),
                        JABATAN.LEVEL_ID.as("level"),
                        BIODATA.NAMA.as("nama_pegawai"),
                        PEGAWAI.NIPAM.as("nipam"),
                        ORGANISASI.NAMA.as("nama_organisasi"),
                        BIODATA.FOTO_PROFIL.as("foto_profil")
                )
                .from(JABATAN)
                .leftJoin(ORGANISASI).on(JABATAN.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(PEGAWAI).on(JABATAN.ID.eq(PEGAWAI.JABATAN_ID)
                        .and(PEGAWAI.STATUS_KERJA.eq((byte) 2))
                        .and(PEGAWAI.IS_DELETED.eq(false)))
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK)
                        .and(BIODATA.IS_DELETED.eq(false)))
                .where(condition)
                .orderBy(JABATAN.LEVEL_ID.asc(), JABATAN.ID.asc())
                .fetch(SoRecordMapper::map);
    }

    public List<SoResponse> fetch() {
        return fetch(null);
    }
}
