package id.perumdamts.kepegawaian.dto.commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    
    @NotNull(message = "Page number cannot be null")
    @Min(value = 0, message = "Page number must be greater than or equal to 0")
    protected Integer page = 0;
    
    @NotNull(message = "Page size cannot be null")
    @Min(value = 1, message = "Page size must be greater than 0")
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
        
        return PageRequest.of(
            page, 
            size, 
            Sort.by(sortDir, sortBy.split(","))
        );
    }
}
