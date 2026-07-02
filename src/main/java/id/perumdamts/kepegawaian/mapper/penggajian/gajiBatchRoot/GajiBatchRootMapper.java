package id.perumdamts.kepegawaian.mapper.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;

public final class GajiBatchRootMapper {
    private GajiBatchRootMapper() {}

    public static GajiBatchRoot toEntityPhase1(GajiBatchRootPostRequest request) {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setId(request.getBatchId());
        entity.setPeriode(request.getPeriode());
        entity.setStatus(EProsesGaji.PENDING);
        entity.setDiProsesOleh(request.getDiProsesOleh());
        entity.setJabatanPemroses(request.getJabatanPemroses());
        return entity;
    }
}
