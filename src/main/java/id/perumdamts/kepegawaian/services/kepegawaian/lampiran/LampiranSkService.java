package id.perumdamts.kepegawaian.services.kepegawaian.lampiran;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkAcceptRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LampiranSkService {
    List<LampiranSkResponse> getLampiran(EJenisSk jenisSk, Long id);

    LampiranSkResponse getLampiranById(Long id);

    ResponseEntity<?> getFileLampiranById(EJenisSk jenisSk, Long id);

    SavedStatus<?> addLampiran(LampiranSkPostRequest request);

    boolean deleteById(Long id);

    SavedStatus<?> acceptLampiran(LampiranSkAcceptRequest request, String oleh);
}
