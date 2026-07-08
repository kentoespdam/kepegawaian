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
        EJenisLampiranProfil ref = r.getRef() != null ? EJenisLampiranProfil.values()[r.getRef()] : null;
        return new LampiranProfilQuery(
                r.getId(),
                ref,
                r.getRefId(),
                r.getFileName(),
                r.getMimeType(),
                r.getNotes(),
                r.getDisetujui(),
                r.getDisetujuiOleh(),
                r.getTanggalDisetujui()
        );
    }
}
