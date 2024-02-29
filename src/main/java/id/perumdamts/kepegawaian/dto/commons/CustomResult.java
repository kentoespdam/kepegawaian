package id.perumdamts.kepegawaian.dto.commons;

import org.springframework.http.ResponseEntity;

import java.util.List;

public class CustomResult {
    public static <T> ResponseEntity<?> any(T data) {
        SingleResult<T> result = new SingleResult<>(data);
        return ResponseEntity.status(result.statusText).body(result);
    }

    public static <T> ResponseEntity<?> list(List<T> data) {
        ListResult<T> result = new ListResult<>(data);
        return ResponseEntity.status(result.statusText).body(result);
    }

    public static <T> ResponseEntity<PageResult<?>> page(T data) {
        return ResponseEntity.ok(new PageResult<>(data));
    }

    public static <T> ResponseEntity<?> save(SavedStatus<T> data) {
        SavedResult<T> result = new SavedResult<>(data);
        return ResponseEntity.status(result.statusText).body(result);
    }

    public static ResponseEntity<?> delete(boolean data) {
        DeletedResult result = new DeletedResult(data);
        return ResponseEntity.status(result.statusText).body(result);
    }

}
