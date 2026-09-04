package id.perumdamts.kepegawaian.services.penggajian.gajiKpi;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKpi;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiKpiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test {@link GajiKpiCommandService} — unik (nipam, periode): aktif → conflict,
 * soft-deleted → revive (create-only), soft-delete. Mock {@link GajiKpiRepository},
 * tanpa database.
 */
@ExtendWith(MockitoExtension.class)
class GajiKpiCommandServiceTest {
    private static final String NIPAM = "12345678";
    private static final String PERIODE = "2026-08";
    private static final Double TUNKIN = 2_000_000.0;

    @Mock
    private GajiKpiRepository repository;

    private GajiKpiCommandService service;

    @BeforeEach
    void setUp() {
        service = new GajiKpiCommandService(repository);
    }

    private GajiKpiPostRequest req() {
        GajiKpiPostRequest r = new GajiKpiPostRequest();
        r.setNipam(NIPAM);
        r.setPeriode(PERIODE);
        r.setTunkin(TUNKIN);
        r.setPph21Ter(10_000.0);
        return r;
    }

    private GajiKpiPutRequest putReq() {
        GajiKpiPutRequest r = new GajiKpiPutRequest();
        r.setNipam(NIPAM);
        r.setPeriode(PERIODE);
        r.setTunkin(TUNKIN);
        r.setPph21Ter(10_000.0);
        return r;
    }

    private GajiKpi entity(Long id, boolean isDeleted) {
        GajiKpi e = new GajiKpi();
        e.setId(id);
        e.setNipam(NIPAM);
        e.setPeriode(PERIODE);
        e.setTunkin(TUNKIN);
        e.setIsDeleted(isDeleted);
        return e;
    }

    @Test
    void save_creates_whenPairNotExists() {
        when(repository.findAnyByNipamAndPeriode(NIPAM, PERIODE)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> {
            GajiKpi e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });

        SavedStatus<Long> result = service.save(req());

        assertEquals(ESaveStatus.SUCCESS, result.getStatus());
        assertEquals(1L, result.getData());
        ArgumentCaptor<GajiKpi> captor = ArgumentCaptor.forClass(GajiKpi.class);
        verify(repository).save(captor.capture());
        assertEquals(NIPAM, captor.getValue().getNipam());
        assertEquals(PERIODE, captor.getValue().getPeriode());
        assertEquals(TUNKIN, captor.getValue().getTunkin());
        assertFalse(captor.getValue().getIsDeleted());
    }

    @Test
    void save_throwsConflict_whenPairActive() {
        when(repository.findAnyByNipamAndPeriode(NIPAM, PERIODE)).thenReturn(Optional.of(entity(1L, false)));

        ConflictException ex = assertThrows(ConflictException.class, () -> service.save(req()));

        assertTrue(ex.getMessage().contains("Gaji KPI sudah ada"),
                "Message must mention 'Gaji KPI sudah ada', got: " + ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void save_revives_whenPairArchived() {
        GajiKpi archived = entity(7L, true);
        archived.setTunkin(111.0);
        when(repository.findAnyByNipamAndPeriode(NIPAM, PERIODE)).thenReturn(Optional.of(archived));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SavedStatus<Long> result = service.save(req());

        assertEquals(ESaveStatus.SUCCESS, result.getStatus());
        assertEquals(7L, result.getData(), "revive harus memakai id row lama, bukan insert baru");
        assertEquals(TUNKIN, archived.getTunkin(), "field harus di-update dari request");
        assertFalse(archived.getIsDeleted(), "row harus dihidupkan kembali");
        verify(repository).save(archived);
    }

    @Test
    void update_throwsNotFound_whenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.update(99L, putReq()));

        assertTrue(ex.getMessage().contains("Gaji KPI not found"),
                "Message must mention 'Gaji KPI not found', got: " + ex.getMessage());
        verify(repository).findById(99L);
        verify(repository, never()).save(any());
    }

    @Test
    void update_succeeds_whenPairIsSelf() {
        GajiKpi self = entity(1L, false);
        when(repository.findById(1L)).thenReturn(Optional.of(self));
        when(repository.findByNipamAndPeriode(NIPAM, PERIODE)).thenReturn(Optional.of(self));
        when(repository.findAnyByNipamAndPeriode(NIPAM, PERIODE)).thenReturn(Optional.of(self));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SavedStatus<Long> result = service.update(1L, putReq());

        assertEquals(ESaveStatus.SUCCESS, result.getStatus());
        assertEquals(1L, result.getData());
        verify(repository).save(self);
    }

    @Test
    void update_throwsConflict_whenPairActiveOwnedByOther() {
        when(repository.findById(2L)).thenReturn(Optional.of(entity(2L, false)));
        when(repository.findByNipamAndPeriode(NIPAM, PERIODE)).thenReturn(Optional.of(entity(1L, false)));

        ConflictException ex = assertThrows(ConflictException.class,
                () -> service.update(2L, putReq()));

        assertTrue(ex.getMessage().contains("Gaji KPI sudah ada"),
                "Message must mention 'Gaji KPI sudah ada', got: " + ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void update_throwsConflict_whenPairArchivedByOther() {
        when(repository.findById(2L)).thenReturn(Optional.of(entity(2L, false)));
        when(repository.findByNipamAndPeriode(NIPAM, PERIODE)).thenReturn(Optional.empty());
        when(repository.findAnyByNipamAndPeriode(NIPAM, PERIODE)).thenReturn(Optional.of(entity(1L, true)));

        ConflictException ex = assertThrows(ConflictException.class,
                () -> service.update(2L, putReq()));

        assertTrue(ex.getMessage().contains("diarsip"),
                "Message must mention archived collision, got: " + ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void delete_softDeletes_whenExists() {
        GajiKpi entity = entity(1L, false);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Boolean result = service.delete(1L);

        assertTrue(result);
        assertTrue(entity.getIsDeleted());
        verify(repository).save(entity);
    }

    @Test
    void delete_returnsFalse_whenMissing() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertFalse(service.delete(1L));
        verify(repository, never()).save(any());
    }
}
