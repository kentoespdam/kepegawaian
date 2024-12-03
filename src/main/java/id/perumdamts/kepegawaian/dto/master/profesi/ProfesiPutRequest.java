package id.perumdamts.kepegawaian.dto.master.profesi;

import id.perumdamts.kepegawaian.entities.master.Grade;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfesiPutRequest extends ProfesiPostRequest {
    public static Profesi toEntity(Profesi entity, ProfesiPostRequest request, Organisasi organisasi, Jabatan jabatan, Grade grade) {
        entity.setOrganisasi(organisasi);
        entity.setJabatan(jabatan);
        entity.setLevel(jabatan.getLevel());
        entity.setGrade(grade);
        entity.setNama(request.getNama());
        entity.setDetail(request.getDetail());
        entity.setResiko(request.getResiko());
        return entity;
    }
}
