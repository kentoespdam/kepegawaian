package id.perumdamts.kepegawaian.repositories.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.SoResponse;
import id.perumdamts.kepegawaian.mapper.laporan.kepegawaian.SoRecordMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

@Repository
@RequiredArgsConstructor
public class SoRepository {
    private final DSLContext dsl;

    public List<SoResponse> fetch() {
        return dsl.select(
                        JABATAN.ID.as("key"),
                        DSL.coalesce(JABATAN.PARENT_ID, DSL.val(0L)).as("boss"),
                        JABATAN.LEVEL_ID.as("level"),
                        JABATAN.NAMA.as("jabatan"),
                        DSL.coalesce(BIODATA.NAMA, DSL.val("")).as("name"),
                        DSL.coalesce(PEGAWAI.NIPAM, DSL.val("")).as("nik")
                )
                .from(JABATAN)
                .leftJoin(PEGAWAI).on(JABATAN.ID.eq(PEGAWAI.JABATAN_ID)
                        .and(PEGAWAI.STATUS_KERJA.eq((byte) 2))) // KARYAWAN_AKTIF
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .where(JABATAN.IS_DELETED.eq(false))
                        .and(JABATAN.LEVEL_ID.le(6L))
                .fetch(SoRecordMapper::map);
    }
}
