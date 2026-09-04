package id.perumdamts.kepegawaian.repositories.penggajian.jdbc;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GajiBatchMasterProsesJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_BATCH_INSERT = """
            INSERT INTO gaji_batch_master_proses
              (batch_master_id, kode, urut, nama, jenis_gaji, nilai, formula, nilai_formula)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

    public void batchInsert(List<GajiBatchMasterProses> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate(SQL_BATCH_INSERT, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                GajiBatchMasterProses p = list.get(i);
                if (p.getBatchMasterId() != null) {
                    ps.setLong(1, p.getBatchMasterId());
                } else {
                    ps.setNull(1, java.sql.Types.BIGINT);
                }
                ps.setString(2, p.getKode());
                if (p.getUrut() != null) {
                    ps.setInt(3, p.getUrut());
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                ps.setString(4, p.getNama());
                ps.setString(5, p.getJenisGaji() != null ? p.getJenisGaji().name() : null);
                ps.setObject(6, p.getNilai());
                ps.setString(7, p.getFormula());
                ps.setString(8, p.getNilaiFormula());
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }
}
