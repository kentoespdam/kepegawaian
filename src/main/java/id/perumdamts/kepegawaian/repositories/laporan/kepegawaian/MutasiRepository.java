package id.perumdamts.kepegawaian.repositories.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.MutasiResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.mapper.laporan.kepegawaian.MutasiRecordMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.RiwayatMutasi.RIWAYAT_MUTASI;

@Repository
@RequiredArgsConstructor
public class MutasiRepository {
    private final DSLContext dsl;

    public List<MutasiResponse> fetch(LocalDate fromDate, LocalDate toDate, EJenisMutasi jenisMutasi) {
        Condition condition = RIWAYAT_MUTASI.TMT_BERLAKU.ge(fromDate)
                .and(RIWAYAT_MUTASI.TMT_BERLAKU.le(toDate));

        if (jenisMutasi != null) {
            condition = condition.and(RIWAYAT_MUTASI.JENIS_MUTASI.eq((byte) jenisMutasi.ordinal()));
        }

        return dsl.selectFrom(RIWAYAT_MUTASI)
                .where(condition)
                .fetch(MutasiRecordMapper::map);
    }
}
