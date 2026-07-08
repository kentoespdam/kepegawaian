package id.perumdamts.kepegawaian.dto.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@Slf4j
public class CustomResult {
    public static <T> ResponseEntity<SingleResult<T>> any(T data) {
        SingleResult<T> result = new SingleResult<>(data);
        return ResponseEntity.status(result.statusText).body(result);
    }

    public static <T> ResponseEntity<SingleResult<T>> optional(Optional<T> data) {
        T value = data.orElse(null);
        SingleResult<T> result = new SingleResult<>(value);
        return ResponseEntity.status(result.statusText).body(result);
    }

    public static <T> ResponseEntity<ListResult<T>> list(List<T> data) {
        ListResult<T> result = new ListResult<>(data);
        return ResponseEntity.status(result.statusText).body(result);
    }

    public static <T> ResponseEntity<PageResult<T>> page(T data) {
        return ResponseEntity.ok(new PageResult<>(data));
    }

    public static <T> ResponseEntity<SavedResult<T>> save(SavedStatus<T> data) {
        SavedResult<T> result = new SavedResult<>(data);
        return ResponseEntity.status(result.statusText).body(result);
    }

    public static ResponseEntity<DeletedResult> delete(boolean data) {
        DeletedResult result = new DeletedResult(data);
        return ResponseEntity.status(result.statusText).body(result);
    }

}
