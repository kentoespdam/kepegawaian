package id.perumdamts.kepegawaian.services.penggajian.gajiPotonganTkk;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkResponse;
import org.springframework.data.domain.Page;

public interface GajiPotonganTkkService {
    Page<GajiPotonganTkkResponse> findPage(GajiPotonganTkkRequest request);
    GajiPotonganTkkResponse findById(Long id);
    SavedStatus<?> create(GajiPotonganTkkPostRequest request);
    SavedStatus<?> update(Long id, GajiPotonganTkkPutRequest request);
    Boolean delete(Long id);
}
