package id.perumdamts.kepegawaian.repositories.cuti.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaPegawaiResponse;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaRequest;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaResponse;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaSisa;
import id.perumdamts.kepegawaian.mapper.cuti.CutiKuotaJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.CutiKuota.CUTI_KUOTA;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

@Repository
@RequiredArgsConstructor
public class CutiKuotaQueryRepository {
    private final DSLContext dsl;

    public CutiKuotaPegawaiResponse pageQueryWithPreviousYear(CutiKuotaRequest query) {
        Condition where = baseWhere(query);
        
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), CUTI_KUOTA.ID);
                
        var count = dsl.selectCount().from(CUTI_KUOTA)
                 .leftJoin(PEGAWAI).on(CUTI_KUOTA.PEGAWAI_ID.eq(PEGAWAI.ID))
                 .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                 .where(where).fetchOptional(0, Long.class).orElse(0L);

        var data = dsl.select(
                        CUTI_KUOTA.ID,
                        CUTI_KUOTA.TAHUN,
                        CUTI_KUOTA.KUOTA,
                        CUTI_KUOTA.KUOTA_TERPAKAI,
                        CUTI_KUOTA.KUOTA_TAMBAHAN,
                        CUTI_KUOTA.SISA_KUOTA,
                        CUTI_KUOTA.EXPIRED,
                        PEGAWAI.ID.as("pegawai_id"),
                        PEGAWAI.NIPAM.as("pegawai_nipam"),
                        PEGAWAI.STATUS_PEGAWAI.as("pegawai_status"),
                        BIODATA.NAMA.as("pegawai_nama"),
                        JABATAN.NAMA.as("pegawai_jabatan"),
                        ORGANISASI.NAMA.as("pegawai_organisasi")
                )
                .from(CUTI_KUOTA)
                .leftJoin(PEGAWAI).on(CUTI_KUOTA.PEGAWAI_ID.eq(PEGAWAI.ID))
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.offset())
                .fetch(CutiKuotaJooqMapper::mapToResponse);
                
        Page<CutiKuotaResponse> page = new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
        // Page kosong tetap return response utuh (200 + empty page ber-metadata), bukan null (ADR-0040)
        if (page.isEmpty()) return new CutiKuotaPegawaiResponse(page, List.of());
        
        List<Long> pegawaiIdList = page.getContent().stream().map(c -> c.pegawai().id()).toList();
        
        List<CutiKuotaResponse> kuotaTahunSebelumnya = dsl.select(
                        CUTI_KUOTA.ID,
                        CUTI_KUOTA.TAHUN,
                        CUTI_KUOTA.KUOTA,
                        CUTI_KUOTA.KUOTA_TERPAKAI,
                        CUTI_KUOTA.KUOTA_TAMBAHAN,
                        CUTI_KUOTA.SISA_KUOTA,
                        CUTI_KUOTA.EXPIRED,
                        PEGAWAI.ID.as("pegawai_id"),
                        PEGAWAI.NIPAM.as("pegawai_nipam"),
                        PEGAWAI.STATUS_PEGAWAI.as("pegawai_status"),
                        BIODATA.NAMA.as("pegawai_nama"),
                        JABATAN.NAMA.as("pegawai_jabatan"),
                        ORGANISASI.NAMA.as("pegawai_organisasi")
                )
                .from(CUTI_KUOTA)
                .leftJoin(PEGAWAI).on(CUTI_KUOTA.PEGAWAI_ID.eq(PEGAWAI.ID))
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .where(CUTI_KUOTA.PEGAWAI_ID.in(pegawaiIdList)
                        .and(CUTI_KUOTA.TAHUN.eq(query.getTahun() - 1))
                        .and(CUTI_KUOTA.IS_DELETED.eq(false)))
                .fetch(CutiKuotaJooqMapper::mapToResponse);
                
        return new CutiKuotaPegawaiResponse(page, kuotaTahunSebelumnya);
    }

    public CutiKuotaResponse getById(Long id) {
        return dsl.select(
                        CUTI_KUOTA.ID,
                        CUTI_KUOTA.TAHUN,
                        CUTI_KUOTA.KUOTA,
                        CUTI_KUOTA.KUOTA_TERPAKAI,
                        CUTI_KUOTA.KUOTA_TAMBAHAN,
                        CUTI_KUOTA.SISA_KUOTA,
                        CUTI_KUOTA.EXPIRED,
                        PEGAWAI.ID.as("pegawai_id"),
                        PEGAWAI.NIPAM.as("pegawai_nipam"),
                        PEGAWAI.STATUS_PEGAWAI.as("pegawai_status"),
                        BIODATA.NAMA.as("pegawai_nama"),
                        JABATAN.NAMA.as("pegawai_jabatan"),
                        ORGANISASI.NAMA.as("pegawai_organisasi")
                )
                .from(CUTI_KUOTA)
                .leftJoin(PEGAWAI).on(CUTI_KUOTA.PEGAWAI_ID.eq(PEGAWAI.ID))
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .where(CUTI_KUOTA.ID.eq(id).and(CUTI_KUOTA.IS_DELETED.eq(false)))
                .fetchOne(CutiKuotaJooqMapper::mapToResponse);
    }

    public CutiKuotaSisa findByPegawai(Long pegawaiId, Integer tahun) {
        Integer sisaTahunIni = dsl.select(CUTI_KUOTA.SISA_KUOTA)
                .from(CUTI_KUOTA)
                .where(CUTI_KUOTA.PEGAWAI_ID.eq(pegawaiId)
                        .and(CUTI_KUOTA.TAHUN.eq(tahun))
                        .and(CUTI_KUOTA.IS_DELETED.eq(false)))
                .fetchOne(record -> record.get(CUTI_KUOTA.SISA_KUOTA));
                
        Integer sisaTahunLalu = dsl.select(CUTI_KUOTA.SISA_KUOTA)
                .from(CUTI_KUOTA)
                .where(CUTI_KUOTA.PEGAWAI_ID.eq(pegawaiId)
                        .and(CUTI_KUOTA.TAHUN.eq(tahun - 1))
                        .and(CUTI_KUOTA.EXPIRED.gt(LocalDate.now()))
                        .and(CUTI_KUOTA.IS_DELETED.eq(false)))
                .fetchOne(record -> record.get(CUTI_KUOTA.SISA_KUOTA));
                
        return new CutiKuotaSisa(
                sisaTahunIni != null ? sisaTahunIni : 0,
                sisaTahunLalu != null ? sisaTahunLalu : 0
        );
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "tahun", CUTI_KUOTA.TAHUN,
                "kuota", CUTI_KUOTA.KUOTA,
                "sisaKuota", CUTI_KUOTA.SISA_KUOTA,
                "expired", CUTI_KUOTA.EXPIRED
        );
    }

    private Condition baseWhere(CutiKuotaRequest q) {
        Condition cond = CUTI_KUOTA.IS_DELETED.eq(false);
        if (q.getPegawaiId() != null) {
            cond = cond.and(CUTI_KUOTA.PEGAWAI_ID.eq(q.getPegawaiId()));
        }
        if (q.getNipam() != null) {
            cond = cond.and(PEGAWAI.NIPAM.likeIgnoreCase("%" + q.getNipam() + "%"));
        }
        if (q.getNama() != null) {
            cond = cond.and(BIODATA.NAMA.likeIgnoreCase("%" + q.getNama() + "%"));
        }
        if (q.getTahun() != null) {
            cond = cond.and(CUTI_KUOTA.TAHUN.eq(q.getTahun()));
        }
        if (q.getExpired() != null) {
            cond = cond.and(CUTI_KUOTA.EXPIRED.eq(q.getExpired()));
        }
        return cond;
    }
}
