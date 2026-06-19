package id.perumdamts.kepegawaian.dto.master.organisasi.commons;

import org.jooq.Field;
import org.jooq.SortField;

import java.util.Map;

/**
 * Type-safe whitelist sorter for JOOQ-backed list queries.
 *
 * <p>Caller supplies a {@code Map<String, Field<?>>} of allowed sort
 * keys mapped to the actual generated JOOQ column. Unknown / blank
 * {@code sortBy} resolves to {@code defaultColumn}. Direction defaults to
 * ascending; only an explicit {@code "asc"} (case-insensitive) is treated as
 * ascending, anything else (including {@code null}) is descending.</p>
 *
 * <p>JOOQ's generated columns are {@code TableField<R,T>} where T varies
 * per column (Long, Integer, String, ...). The common supertype with
 * {@code .asc()/.desc()} is {@code Field<?>}; the resolved
 * {@code asc()}/{@code desc()} returns a {@code SortField<?>}, which is
 * what JOOQ's {@code orderBy(...)} consumes.</p>
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
