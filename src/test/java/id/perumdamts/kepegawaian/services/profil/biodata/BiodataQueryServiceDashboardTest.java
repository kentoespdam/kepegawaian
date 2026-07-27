package id.perumdamts.kepegawaian.services.profil.biodata;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataDashboardResponse;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.profil.jooq.BiodataDashboardQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.BiodataDetailQuery;
import id.perumdamts.kepegawaian.repositories.profil.jooq.BiodataQueryRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link BiodataQueryService#getDashboard(String)}.
 * Mocks {@link BiodataDashboardQuery} — no database required.
 */
@ExtendWith(MockitoExtension.class)
class BiodataQueryServiceDashboardTest {

    @Mock
    private BiodataQueryRepository queries;
    @Mock
    private BiodataDetailQuery detail;
    @Mock
    private BiodataDashboardQuery dashboard;
    @Mock
    private BiodataRepository repository;
    @Mock
    private FileUploadUtil fileUploadUtil;

    private BiodataQueryService service;

    @BeforeEach
    void setUp() {
        service = new BiodataQueryService(queries, detail, dashboard, repository, fileUploadUtil);
    }

    @Test
    void getDashboard_returnsResponse() {
        String nik = "1234567890";
        BiodataDashboardResponse expected = new BiodataDashboardResponse(
                nik, "Budi", "Laki-Laki", "Jakarta", null,
                null, null, null, null, "budi@company.com",
                null, null, null);
        when(dashboard.getByNik(nik)).thenReturn(Optional.of(expected));

        BiodataDashboardResponse result = service.getDashboard(nik);

        assertSame(expected, result);
        verify(dashboard).getByNik(nik);
    }

    @Test
    void getDashboard_throwsNotFound_whenBiodataNotExists() {
        String nik = "unknown";
        when(dashboard.getByNik(nik)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.getDashboard(nik));

        assertTrue(ex.getMessage().contains("Biodata not found")
                        || ex.getMessage().contains("bukan pegawai"),
                "Exception message must mention missing biodata or pegawai: " + ex.getMessage());
        verify(dashboard).getByNik(nik);
    }


}
