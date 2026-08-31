package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.SoResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.SoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoService {
    private final SoRepository repository;

    public Map<String, Object> fetch() {
        var flat = repository.fetch();
        return buildHierarchy(flat);
    }

    private Map<String, Object> buildHierarchy(java.util.List<SoResponse> flat) {
        if (flat.isEmpty()) {
            return Map.of("hierarchy", Map.of());
        }
        var nodes = new HashMap<Long, SoResponse>();
        for (var item : flat) {
            nodes.put(item.key(), item);
        }

        SoResponse root = null;
        for (var item : flat) {
            if (item.boss() == 0L) {
                root = item;
                break;
            }
        }
        if (root == null) {
            root = flat.getFirst();
        }

        // Build parent→children relationships using mutable copies
        var childrenMap = new HashMap<Long, java.util.List<SoResponse>>();
        for (var item : flat) {
            if (item.boss() != 0L && nodes.containsKey(item.boss())) {
                childrenMap.computeIfAbsent(item.boss(), k -> new java.util.ArrayList<>()).add(item);
            }
        }

        // Rebuild tree with children
        var rootWithChildren = buildNode(root, childrenMap);
        return Map.of("hierarchy", rootWithChildren);
    }

    private SoResponse buildNode(SoResponse node, Map<Long, java.util.List<SoResponse>> childrenMap) {
        var children = childrenMap.getOrDefault(node.key(), java.util.List.of());
        var childNodes = new java.util.ArrayList<SoResponse>();
        for (var child : children) {
            childNodes.add(buildNode(child, childrenMap));
        }
        return new SoResponse(node.key(), node.boss(), node.level(), node.jabatan(), node.name(), node.nik(), childNodes);
    }
}
