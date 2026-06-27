package id.perumdamts.kepegawaian.dto.pegawai;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class PageRequest implements Serializable {
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    @Min(value = 0, message = "page must be >= 0")
    protected Integer page = 0;

    @Min(value = 1, message = "size must be >= 1")
    @Max(value = MAX_SIZE, message = "size must be <= " + MAX_SIZE)
    protected Integer size = DEFAULT_SIZE;

    protected String sortBy;
    protected String sortDirection = "asc";

    public int getPageNumber() {
        return Objects.isNull(page) || page < 0 ? 0 : page;
    }

    public int getSizeOrDefault() {
        if (Objects.isNull(size) || size <= 0 || size > MAX_SIZE) {
            return DEFAULT_SIZE;
        }
        return size;
    }

    public int offset() {
        return getPageNumber() * getSizeOrDefault();
    }
}
