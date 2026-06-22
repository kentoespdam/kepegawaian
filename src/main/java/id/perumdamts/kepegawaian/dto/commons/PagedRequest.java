package id.perumdamts.kepegawaian.dto.commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Objects;

/**
 * Master-module base for paged, sort-aware list requests.
 *
 * <p>Lives under {@code dto.master.organisasi.commons} (not the global
 * {@code dto.commons}) because the global {@code CommonPageRequest} has no
 * whitelist on {@code sortBy} — we want a type-safe whitelist per module.</p>
 *
 * <p>Constraints: {@code size} is clamped to {@code [1, MAX_SIZE]};
 * {@code page} is non-negative; {@code sortDirection} defaults to
 * {@code "asc"}.</p>
 */
@Getter
@Setter
public abstract class PagedRequest implements Serializable {
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    @Min(value = 0, message = "page must be >= 0")
    protected Integer page = 0;

    @Min(value = 1, message = "size must be >= 1")
    @Max(value = MAX_SIZE, message = "size must be <= " + MAX_SIZE)
    protected Integer size = DEFAULT_SIZE;

    protected String sortBy;

    protected String sortDirection = "asc";

    /**
     * Effective page index (never null, never negative).
     */
    public int getPageNumber() {
        return Objects.isNull(page) || page < 0 ? 0 : page;
    }

    /**
     * Effective size — clamps invalid values to {@link #DEFAULT_SIZE}.
     */
    public int getSizeOrDefault() {
        if (Objects.isNull(size) || size <= 0 || size > MAX_SIZE) {
            return DEFAULT_SIZE;
        }
        return size;
    }

    public int offset() {
        return getPageNumber() * getSizeOrDefault();
    }

    @JsonIgnore
    public Pageable getPageable() {
        String sortKey = (sortBy == null || sortBy.isBlank()) ? "id" : sortBy;
        return PageRequest.of(
                getPageNumber(),
                getSizeOrDefault(),
                Sort.by(resolveDirection(), sortKey.split(","))
        );
    }

    private Sort.Direction resolveDirection() {
        return "desc".equalsIgnoreCase(Objects.requireNonNullElse(sortDirection, "asc").trim())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
    }
}
