package id.perumdamts.kepegawaian.services.master.sanksi;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.sanksi.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SanksiService {
    Page<SanksiResponse> findPage(SanksiRequest request);

    List<SanksiMiniResponse> list();

    SanksiResponse findById(Long id);

    SavedStatus<?> save(SanksiPostRequest request);
    SavedStatus<?> update(Long id, SanksiPutRequest request);

    SavedStatus<?> updateJenisSp(Long id, PatchSanksiJenisSpRequest request);

    boolean delete(Long id);
}
