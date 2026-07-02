package id.perumdamts.kepegawaian.dto.commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Objects;

/**
 * Single pagination base class for CQRS/JOOQ reads.
 *
 * <p>Contract details:
 * <ul>
 *   <li>{@link #getSizeOrDefault()}: Returns size clamped to [1, 100], defaulting to 20.</li>
 *   <li>{@link #offset()}: Calculates page offset based on active page number and size.</li>
 *   <li>{@link #getPageNumber()}: Active page index, defaults to 0 if null or negative.</li>
 *   <li>{@link #getSortBy()}: Target column(s) for sorting.</li>
 *   <li>{@link #getSortDirection()}: "asc" or "desc" (case-insensitive).</li>
 * </ul>
 * </p>
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

    @Pattern(regexp = "(?i)asc|desc", message = "sortDirection must be asc or desc")
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
        if (sortBy == null || sortBy.isBlank()) {
            return PageRequest.of(getPageNumber(), getSizeOrDefault(), Sort.unsorted());
        }
        return PageRequest.of(
                getPageNumber(),
                getSizeOrDefault(),
                Sort.by(resolveDirection(), sortBy.split(","))
        );
    }

    private Sort.Direction resolveDirection() {
        return "desc".equalsIgnoreCase(Objects.requireNonNullElse(sortDirection, "asc").trim())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
    }
}

