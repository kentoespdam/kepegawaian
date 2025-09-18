package id.perumdamts.kepegawaian.dto.commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * A base class for pagination requests with sorting support.
 * Provides common pagination parameters and conversion to Spring's Pageable.
 */
@Data
public class CommonPageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected Integer page = 0;

    protected Integer size = 10;

    protected String sortBy;

    @Pattern(regexp = "asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Sort direction must be 'asc' or 'desc'")
    protected String sortDirection = "asc";

    /**
     * Converts this request to a Spring Pageable object for use in repositories.
     *
     * @return a Pageable instance with pagination and sorting parameters
     */
    @JsonIgnore
    public Pageable getPageable() {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return PageRequest.of(page, size);
        }

        String direction = Objects.requireNonNullElse(sortDirection, "asc").toLowerCase();
        Sort.Direction sortDir = "desc".equals(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        int currentPage = Objects.isNull(page) ? 0 : page;
        int currentSize = Objects.isNull(size) ? 10 : size;

        return PageRequest.of(
                currentPage,
                currentSize,
                Sort.by(sortDir, sortBy.split(","))
        );
    }
}
