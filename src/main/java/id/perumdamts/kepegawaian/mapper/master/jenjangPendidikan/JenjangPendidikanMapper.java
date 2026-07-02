package id.perumdamts.kepegawaian.mapper.master.jenjangPendidikan;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanPutRequest;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;

public final class JenjangPendidikanMapper {
    private JenjangPendidikanMapper() {}

    public static JenjangPendidikan toEntity(JenjangPendidikanPostRequest request) {
        return new JenjangPendidikan(
                request.getNama(),
                request.getShortName(),
                request.getSeq(),
                request.getIsStatistik()
        );
    }

    public static void updateEntity(JenjangPendidikan entity, JenjangPendidikanPutRequest request) {
        entity.setNama(request.getNama());
        entity.setShortName(request.getShortName());
        entity.setSeq(request.getSeq());
        entity.setIsStatistik(request.getIsStatistik());
    }
}
