package id.perumdamts.kepegawaian.mapper.profil.lampiranProfil;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilQuery;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import org.jooq.Record;
import org.jooq.RecordMapper;

import static id.perumdamts.kepegawaian.jooq.tables.LampiranProfil.LAMPIRAN_PROFIL;

public final class LampiranProfilJooqMapper implements RecordMapper<Record, LampiranProfilQuery> {
    public static final LampiranProfilJooqMapper INSTANCE = new LampiranProfilJooqMapper();

    private LampiranProfilJooqMapper() {}

    @Override
    public LampiranProfilQuery map(Record record) {
        var r = record.into(LAMPIRAN_PROFIL);
        LampiranProfilQuery q = new LampiranProfilQuery();
        q.setId(r.getId());
        if (r.getRef() != null) {
            q.setRef(EJenisLampiranProfil.values()[r.getRef()]);
        }
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
