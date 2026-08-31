package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.SoResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.SoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SoServiceTest {

    @Mock private SoRepository repository;
    @InjectMocks private SoService service;

    @Test
    void buildHierarchyCreatesTree() {
        var root = new SoResponse(1L, 0L, 1, "Direktur", "Andi", "8903001", List.of());
        var child = new SoResponse(2L, 1L, 2, "Manager SDM", "Budi", "8903002", List.of());
        var grandchild = new SoResponse(3L, 2L, 3, "Staff HRD", "Citra", "8903003", List.of());
        when(repository.fetch()).thenReturn(List.of(root, child, grandchild));

        var result = service.fetch();

        @SuppressWarnings("unchecked")
        var hierarchy = (SoResponse) result.get("hierarchy");
        assertNotNull(hierarchy);
        assertEquals(1L, hierarchy.key());
        assertEquals("Direktur", hierarchy.jabatan());
        assertEquals(1, hierarchy.subordinates().size());
        assertEquals("Manager SDM", hierarchy.subordinates().getFirst().jabatan());
        assertEquals(1, hierarchy.subordinates().getFirst().subordinates().size());
        assertEquals("Staff HRD", hierarchy.subordinates().getFirst().subordinates().getFirst().jabatan());
    }

    @Test
    void buildHierarchyEmptyReturnsEmptyMap() {
        when(repository.fetch()).thenReturn(List.of());
        var result = service.fetch();
        assertNotNull(result.get("hierarchy"));
    }
}
