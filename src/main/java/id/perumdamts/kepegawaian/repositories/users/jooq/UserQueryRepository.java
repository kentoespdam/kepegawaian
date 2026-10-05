package id.perumdamts.kepegawaian.repositories.users.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.users.UserProjection;
import id.perumdamts.kepegawaian.dto.users.UserRequest;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {
    private final DSLContext dsl;

    public Page<UserProjection> findPage(UserRequest request) {
        var sortOrder = SortParam.resolve(request.getSortBy(), request.getSortDirection(), allowedSorts(), PEGAWAI.ID);
        Condition where = baseWhere(request);

        var count = dsl.selectCount()
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .where(where)
                .fetchOptional(0, Long.class)
                .orElse(0L);

        var data = dsl.select(
                        PEGAWAI.ID,
                        PEGAWAI.NIPAM,
                        BIODATA.NAMA
                )
                .from(PEGAWAI)
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .where(where)
                .orderBy(sortOrder)
                .limit(request.getSizeOrDefault())
                .offset(request.getPageNumber() * request.getSizeOrDefault())
                .fetchInto(UserProjection.class);

        return new PageImpl<>(data, PageRequest.of(request.getPageNumber(), request.getSizeOrDefault()), count);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "id", PEGAWAI.ID,
                "nipam", PEGAWAI.NIPAM,
                "nama", BIODATA.NAMA,
                "biodata.nama", BIODATA.NAMA,
                "statusKerja", PEGAWAI.STATUS_KERJA
        );
    }

    private Condition baseWhere(UserRequest q) {
        Condition condition = PEGAWAI.IS_DELETED.eq(false);
        if (q.getNipam() != null && !q.getNipam().isBlank()) {
            condition = condition.and(PEGAWAI.NIPAM.likeIgnoreCase("%" + q.getNipam() + "%"));
        }
        if (q.getNama() != null && !q.getNama().isBlank()) {
            condition = condition.and(BIODATA.NAMA.likeIgnoreCase("%" + q.getNama() + "%"));
        }
        if (q.getStatusKerja() != null) {
            condition = condition.and(PEGAWAI.STATUS_KERJA.eq((byte) q.getStatusKerja().ordinal()));
        }
        return condition;
    }
}
