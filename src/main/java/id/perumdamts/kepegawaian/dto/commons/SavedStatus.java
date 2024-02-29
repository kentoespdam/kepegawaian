package id.perumdamts.kepegawaian.dto.commons;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavedStatus<T> {
    private final T data;
    @Enumerated(EnumType.STRING)
    private ESaveStatus status;

    public SavedStatus(T data, ESaveStatus status) {
        this.data = data;
        this.status = status;
    }

    public static <T> SavedStatus<T> build(ESaveStatus status, T data) {
        return new SavedStatus<>(data, status);
    }
}
