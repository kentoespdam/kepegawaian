package id.perumdamts.kepegawaian.services.master.profesi;

import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiPostRequest;
import id.perumdamts.kepegawaian.entities.master.Grade;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;

import java.util.Objects;

public final class ProfesiMapper {
    private ProfesiMapper() {}

    public static Profesi toEntity(ProfesiPostRequest request,
                                   Organisasi organisasi,
                                   Jabatan jabatan,
                                   Grade grade) {
        Profesi entity = new Profesi();
        entity.setOrganisasi(organisasi);
        entity.setJabatan(jabatan);
        entity.setLevel(jabatan.getLevel());
        entity.setGrade(grade);
        entity.setNama(request.getNama());
        entity.setDetail(request.getDetail());
        entity.setResiko(request.getResiko());
        return entity;
    }

    public static void updateEntity(Profesi entity,
                                    ProfesiPostRequest request,
                                    Organisasi organisasi,
                                    Jabatan jabatan,
                                    Grade grade) {
        entity.setOrganisasi(organisasi);
        entity.setJabatan(jabatan);
        entity.setLevel(jabatan.getLevel());
        entity.setGrade(grade);
        entity.setNama(request.getNama());
        entity.setDetail(request.getDetail());
        entity.setResiko(request.getResiko());
    }

    public static void setLevelFromJabatan(Profesi entity, Jabatan jabatan) {
        if (Objects.nonNull(jabatan)) {
            entity.setLevel(jabatan.getLevel());
        }
    }
}
