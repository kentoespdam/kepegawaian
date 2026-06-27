package id.perumdamts.kepegawaian.dto.pegawai;

import org.jooq.Field;
import org.jooq.SortField;

import java.util.Map;

/**
 * Whitelist sorter for JOOQ-backed list queries in Pegawai module.
 */
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
