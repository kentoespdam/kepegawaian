package id.perumdamts.kepegawaian.dto.commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
public class CommonPageRequest implements Serializable {
    protected Integer page = 0;
    protected Integer size = 10;
    protected String sortBy;
    protected String sortDirection = "asc";

    @JsonIgnore
    public Pageable getPageable() {
        if (sortBy == null || sortBy.isEmpty())
            return PageRequest.of(page, size);
        return PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
    }
}
