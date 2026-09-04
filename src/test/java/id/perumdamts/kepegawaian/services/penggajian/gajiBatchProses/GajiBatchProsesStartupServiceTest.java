package id.perumdamts.kepegawaian.services.penggajian.gajiBatchProses;

import id.perumdamts.kepegawaian.entities.commons.EJenisErrorGaji;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootErrorLogs;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchRootRepository;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot.GajiBatchRootEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GajiBatchProsesStartupServiceTest {

    @Mock GajiBatchRootRepository repository;
    @Mock GajiBatchRootEventPublisher eventPublisher;

    private GajiBatchProsesStartupService service;

    @BeforeEach
    void setUp() {
        service = new GajiBatchProsesStartupService(repository, eventPublisher);
    }

    private GajiBatchRoot batch(String id, EProsesGaji status) {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setId(id);
        entity.setStatus(status);
        return entity;
    }

    @Test
    void recoverProses_batchProsesMenjadiFailed_denganErrorSystem() {
        GajiBatchRoot proses = batch("b1", EProsesGaji.PROSES);
        when(repository.findByStatus(EProsesGaji.PROSES)).thenReturn(List.of(proses));
        when(repository.findByStatus(EProsesGaji.PENDING)).thenReturn(List.of());

        service.onApplicationEvent(null);

        assertEquals(EProsesGaji.FAILED, proses.getStatus());
        assertEquals(1, proses.getErrorLogs().size());
        GajiBatchRootErrorLogs error = proses.getErrorLogs().iterator().next();
        assertEquals(EJenisErrorGaji.SYSTEM, error.getJenisError());
        assertTrue(error.getNotes().contains("Server restart detected"));
        verify(repository).saveAll(List.of(proses));
        verify(eventPublisher, never()).publishAfterCommit(anyString());
    }

    @Test
    void recoverProses_tanpaBatchProses_tidakMenyentuhSave() {
        when(repository.findByStatus(EProsesGaji.PROSES)).thenReturn(List.of());
        when(repository.findByStatus(EProsesGaji.PENDING)).thenReturn(List.of());

        service.onApplicationEvent(null);

        verify(repository, never()).saveAll(any());
    }

    @Test
    void requeuePending_publishUlangEvent() {
        GajiBatchRoot pending1 = batch("b1", EProsesGaji.PENDING);
        GajiBatchRoot pending2 = batch("b2", EProsesGaji.PENDING);
        when(repository.findByStatus(EProsesGaji.PROSES)).thenReturn(List.of());
        when(repository.findByStatus(EProsesGaji.PENDING)).thenReturn(List.of(pending1, pending2));

        service.onApplicationEvent(null);

        verify(eventPublisher).publishAfterCommit("b1");
        verify(eventPublisher).publishAfterCommit("b2");
        // batch PENDING tidak diubah statusnya di sini — diproses ulang oleh listener
        assertEquals(EProsesGaji.PENDING, pending1.getStatus());
        verify(repository, never()).saveAll(any());
    }

    @Test
    void requeuePending_kosong_tidakPublish() {
        when(repository.findByStatus(EProsesGaji.PROSES)).thenReturn(List.of());
        when(repository.findByStatus(EProsesGaji.PENDING)).thenReturn(List.of());

        service.onApplicationEvent(null);

        verify(eventPublisher, never()).publishAfterCommit(anyString());
    }
}