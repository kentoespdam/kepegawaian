package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.SoResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.SoRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoService {
    private final SoRepository repository;

    public List<SoResponse> fetchFlat(Integer maxLevel) {
        int level = (maxLevel == null || maxLevel <= 0) ? 6 : maxLevel;
        return repository.fetch(level);
    }

    public Map<String, Object> fetchTree(Integer maxLevel) {
        var flat = fetchFlat(maxLevel);
        return buildHierarchy(flat);
    }

    public Map<String, Object> fetch() {
        return fetchTree(6);
    }

    private Map<String, Object> buildHierarchy(List<SoResponse> flat) {
        if (flat == null || flat.isEmpty()) {
            return Map.of("hierarchy", Map.of());
        }

        var nodes = new HashMap<Long, SoResponse>();
        for (var item : flat) {
            nodes.put(item.id(), item);
        }

        var roots = new ArrayList<SoResponse>();
        for (var item : flat) {
            if (item.parentId() == null || item.parentId() == 0L || !nodes.containsKey(item.parentId())) {
                roots.add(item);
            }
        }

        if (roots.isEmpty()) {
            roots.add(flat.getFirst());
        }

        var childrenMap = new HashMap<Long, List<SoResponse>>();
        for (var item : flat) {
            if (item.parentId() != null && item.parentId() != 0L && nodes.containsKey(item.parentId())) {
                childrenMap.computeIfAbsent(item.parentId(), k -> new ArrayList<>()).add(item);
            }
        }

        if (roots.size() == 1) {
            var rootWithChildren = buildNode(roots.getFirst(), childrenMap);
            return Map.of("hierarchy", rootWithChildren);
        }

        var rootsWithChildren = roots.stream().map(r -> buildNode(r, childrenMap)).toList();
        return Map.of("hierarchy", rootsWithChildren);
    }

    private SoResponse buildNode(SoResponse node, Map<Long, List<SoResponse>> childrenMap) {
        var children = childrenMap.getOrDefault(node.id(), List.of());
        var childNodes = new ArrayList<SoResponse>();
        for (var child : children) {
            childNodes.add(buildNode(child, childrenMap));
        }
        return node.withChildren(childNodes);
    }
}
