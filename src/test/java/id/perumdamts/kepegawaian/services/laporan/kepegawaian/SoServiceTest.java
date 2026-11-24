package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.SoResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.SoRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SoServiceTest {

    @Mock
    private SoRepository repository;

    @InjectMocks
    private SoService service;

    @Test
    void fetchFlatDelegatesToRepository() {
        when(repository.fetch(null)).thenReturn(List.of());
        when(repository.fetch(3)).thenReturn(List.of());

        service.fetchFlat();
        service.fetchFlat(null);
        service.fetchFlat(3);

        verify(repository, times(2)).fetch(null);
        verify(repository, times(1)).fetch(3);
    }

    @Test
    void fetchTreeBuildsHierarchySingleRoot() {
        var root = SoResponse.flat(1L, null, "DIR", "Direktur", 1, "Andi", "8903001", "Direksi", null, false);
        var child = SoResponse.flat(2L, 1L, "MGR", "Manager SDM", 2, "Budi", "8903002", "Bagian SDM", null, false);
        var grandchild = SoResponse.flat(3L, 2L, "STF", "Staff HRD", 3, null, null, "Sub Bagian SDM", null, true);

        when(repository.fetch(6)).thenReturn(List.of(root, child, grandchild));

        var result = service.fetchTree(6);

        var hierarchy = (SoResponse) result.get("hierarchy");
        assertNotNull(hierarchy);
        assertEquals(1L, hierarchy.id());
        assertEquals("Direktur", hierarchy.namaJabatan());
        assertEquals(1, hierarchy.children().size());

        var childNode = hierarchy.children().getFirst();
        assertEquals("Manager SDM", childNode.namaJabatan());
        assertEquals(1, childNode.children().size());

        var grandchildNode = childNode.children().getFirst();
        assertEquals("Staff HRD", grandchildNode.namaJabatan());
        assertTrue(grandchildNode.isLowong());
        assertTrue(grandchildNode.children().isEmpty());
    }

    @Test
    void fetchTreeMultiRootReturnsList() {
        var root1 = SoResponse.flat(1L, null, "DEWAS", "Dewan Pengawas", 1, "Agus", "111", "Dewan Pengawas", null, false);
        var root2 = SoResponse.flat(2L, null, "DIRUT", "Direktur Utama", 1, "Bambang", "222", "Direksi", null, false);

        when(repository.fetch(6)).thenReturn(List.of(root1, root2));

        var result = service.fetchTree(6);

        @SuppressWarnings("unchecked")
        var hierarchyList = (List<SoResponse>) result.get("hierarchy");
        assertNotNull(hierarchyList);
        assertEquals(2, hierarchyList.size());
        assertEquals("Dewan Pengawas", hierarchyList.get(0).namaJabatan());
        assertEquals("Direktur Utama", hierarchyList.get(1).namaJabatan());
    }

    @Test
    void fetchTreeEmptyReturnsEmptyMap() {
        when(repository.fetch(6)).thenReturn(List.of());

        var result = service.fetchTree(6);

        assertNotNull(result.get("hierarchy"));
        assertTrue(result.get("hierarchy") instanceof java.util.Map);
    }

    @Test
    void fetchBackwardsCompatibilityDelegatesToFetchTree() {
        var root = SoResponse.flat(1L, null, "DIR", "Direktur", 1, "Andi", "8903001", "Direksi", null, false);
        when(repository.fetch(null)).thenReturn(List.of(root));

        var result = service.fetch();

        assertNotNull(result.get("hierarchy"));
        verify(repository).fetch(null);
    }

    @Test
    void fetchTreeNoArgFetchesAllLevels() {
        var root = SoResponse.flat(1L, null, "DIR", "Direktur", 1, "Andi", "8903001", "Direksi", null, false);
        when(repository.fetch(null)).thenReturn(List.of(root));

        var result = service.fetchTree();

        assertNotNull(result.get("hierarchy"));
        verify(repository).fetch(null);
    }

    @Test
    void fetchTreeListNoArgFetchesAllLevels() {
        var root = SoResponse.flat(1L, null, "DIR", "Direktur", 1, "Andi", "8903001", "Direksi", null, false);
        when(repository.fetch(null)).thenReturn(List.of(root));

        var result = service.fetchTreeList();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).fetch(null);
    }

    @Test
    void fetchTreeListBuildsHierarchyRootNodes() {
        var root = SoResponse.flat(1L, null, "DIR", "Direktur", 1, "Andi", "8903001", "Direksi", null, false);
        var child = SoResponse.flat(2L, 1L, "MGR", "Manager SDM", 2, "Budi", "8903002", "Bagian SDM", null, false);
        var grandchild = SoResponse.flat(3L, 2L, "STF", "Staff HRD", 3, null, null, "Sub Bagian SDM", null, true);

        when(repository.fetch(6)).thenReturn(List.of(root, child, grandchild));

        var result = service.fetchTreeList(6);

        assertNotNull(result);
        assertEquals(1, result.size());
        var rootNode = result.getFirst();
        assertEquals(1L, rootNode.id());
        assertEquals("Direktur", rootNode.namaJabatan());
        assertEquals(1, rootNode.children().size());

        var childNode = rootNode.children().getFirst();
        assertEquals("Manager SDM", childNode.namaJabatan());
        assertEquals(1, childNode.children().size());

        var grandchildNode = childNode.children().getFirst();
        assertEquals("Staff HRD", grandchildNode.namaJabatan());
        assertTrue(grandchildNode.isLowong());
        assertTrue(grandchildNode.children().isEmpty());
    }

    @Test
    void fetchTreeListEmptyReturnsEmptyList() {
        when(repository.fetch(6)).thenReturn(List.of());

        var result = service.fetchTreeList(6);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
