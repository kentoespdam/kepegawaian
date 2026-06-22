package id.perumdamts.kepegawaian.dto.master.profesi.commons;

import org.jooq.Field;
import org.jooq.SortField;

import java.util.Map;

public final class SortParam {
    private SortParam() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static SortField<?> resolve(String sortBy,
                                       String sortDir,
                                       Map<String, Field<?>> allowedSorts,
                                       Field<?> defaultColumn) {
        Field<?> sortField = (sortBy == null
                || sortBy.isBlank()
                || !allowedSorts.containsKey(sortBy))
                ? defaultColumn
                : allowedSorts.get(sortBy);
        boolean asc = sortDir != null && "asc".equalsIgnoreCase(sortDir.trim());
        return (SortField) (asc ? sortField.asc() : sortField.desc());
    }
}
