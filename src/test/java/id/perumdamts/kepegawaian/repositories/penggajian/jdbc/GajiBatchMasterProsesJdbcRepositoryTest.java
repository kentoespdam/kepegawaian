package id.perumdamts.kepegawaian.repositories.penggajian.jdbc;

import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GajiBatchMasterProsesJdbcRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private GajiBatchMasterProsesJdbcRepository repository;

    @BeforeEach
    void setUp() {
        repository = new GajiBatchMasterProsesJdbcRepository(jdbcTemplate);
    }

    @Test
    void batchInsert_emptyList_noOp() {
        repository.batchInsert(List.of());
        repository.batchInsert(null);
        verify(jdbcTemplate, never()).batchUpdate(anyString(), any(BatchPreparedStatementSetter.class));
    }

    @Test
    void batchInsert_withData_executesBatchUpdate() throws SQLException {
        GajiBatchMasterProses p1 = new GajiBatchMasterProses(
                1L, 100L, "GP", 1, "Gaji Pokok", EJenisGaji.PEMASUKAN, 5000000.0, "#SYSTEM", "5000000");
        GajiBatchMasterProses p2 = new GajiBatchMasterProses(
                null, null, "POT_LAIN", null, "Potongan Lain", null, null, null, null);

        repository.batchInsert(List.of(p1, p2));

        ArgumentCaptor<BatchPreparedStatementSetter> captor = ArgumentCaptor.forClass(BatchPreparedStatementSetter.class);
        verify(jdbcTemplate).batchUpdate(anyString(), captor.capture());

        BatchPreparedStatementSetter setter = captor.getValue();
        assertEquals(2, setter.getBatchSize());

        PreparedStatement ps1 = mock(PreparedStatement.class);
        setter.setValues(ps1, 0);
        verify(ps1).setLong(1, 100L);
        verify(ps1).setString(2, "GP");
        verify(ps1).setInt(3, 1);
        verify(ps1).setString(4, "Gaji Pokok");
        verify(ps1).setString(5, "PEMASUKAN");
        verify(ps1).setObject(6, 5000000.0);
        verify(ps1).setString(7, "#SYSTEM");
        verify(ps1).setString(8, "5000000");

        PreparedStatement ps2 = mock(PreparedStatement.class);
        setter.setValues(ps2, 1);
        verify(ps2).setNull(1, java.sql.Types.BIGINT);
        verify(ps2).setString(2, "POT_LAIN");
        verify(ps2).setNull(3, java.sql.Types.INTEGER);
        verify(ps2).setString(4, "Potongan Lain");
        verify(ps2).setString(5, null);
        verify(ps2).setObject(6, null);
        verify(ps2).setString(7, null);
        verify(ps2).setString(8, null);
    }
}
