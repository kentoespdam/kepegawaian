package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.preload;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;

import java.util.List;

public record HitungPegawaiResult(
        GajiBatchMaster master,
        List<GajiBatchMasterProses> prosesList,
        ErrorEntry error
) {
    public boolean isSuccess() {
        return error == null;
    }
}
