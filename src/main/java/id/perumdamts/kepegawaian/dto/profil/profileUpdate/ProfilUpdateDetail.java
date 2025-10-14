package id.perumdamts.kepegawaian.dto.profil.profileUpdate;

import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import lombok.Data;
import org.springframework.data.history.Revisions;

@Data
public class ProfilUpdateDetail {
//    private Long id;
//    private String nipam;
//    private String nama;
//    private String jabatan;
//    private LocalDateTime tanggalPengajuan;
//    private RevisionMetadata.RevisionType actionType;
//    private String dataDescription;
//    private EProfileUpdateApproval approvalStatus;
//    private Object oldData;
//    private Object newData;
//
//    public static ProfilUpdateDetail from(ProfileUpdate entity) {
//        ProfilUpdateDetail result = new ProfilUpdateDetail();
//        result.setId(entity.getId());
//        result.setNipam(entity.getNipam());
//        result.setNama(entity.getNama());
//        result.setJabatan(entity.getJabatan());
//        result.setTanggalPengajuan(entity.getReqDate());
//        result.setDataDescription(entity.getDataDescription());
//        result.setApprovalStatus(entity.getApprovalStatus());
//        return result;
//    }

    private ProfileUpdate profileUpdate;
    private Revisions<Integer, ?> revisions;

    public static ProfilUpdateDetail from(ProfileUpdate profileUpdate, Revisions<Integer, ?> revisions) {
        ProfilUpdateDetail result = new ProfilUpdateDetail();
        result.setProfileUpdate(profileUpdate);
        result.setRevisions(revisions);
        return result;
    }
}
