package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilQuery;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.LampiranProfil.LAMPIRAN_PROFIL;

@Repository
@RequiredArgsConstructor
public class LampiranProfilQueryRepository {
    private final DSLContext dsl;

    public List<LampiranProfilQuery> findByRefAndRefId(EJenisLampiranProfil ref, Long refId) {
        return dsl.selectFrom(LAMPIRAN_PROFIL)
                .where(LAMPIRAN_PROFIL.REF.eq((byte) ref.ordinal()))
                .and(LAMPIRAN_PROFIL.REF_ID.eq(refId))
                .and(LAMPIRAN_PROFIL.IS_DELETED.eq(false))
                .fetch(this::toQuery);
    }

    public Optional<LampiranProfilQuery> getById(Long id) {
        return dsl.selectFrom(LAMPIRAN_PROFIL)
                .where(LAMPIRAN_PROFIL.ID.eq(id))
                .and(LAMPIRAN_PROFIL.IS_DELETED.eq(false))
                .fetchOptional(this::toQuery);
    }

    private LampiranProfilQuery toQuery(org.jooq.Record record) {
        var r = record.into(LAMPIRAN_PROFIL);
        LampiranProfilQuery q = new LampiranProfilQuery();
        q.setId(r.getId());
        q.setRef(EJenisLampiranProfil.values()[r.getRef()]);
        q.setRefId(r.getRefId());
        q.setFileName(r.getFileName());
        q.setMimeType(r.getMimeType());
        q.setNotes(r.getNotes());
        q.setDisetujui(r.getDisetujui());
        q.setDisetujuiOleh(r.getDisetujuiOleh());
        q.setTanggalDisetujui(r.getTanggalDisetujui());
        return q;
    }
}
