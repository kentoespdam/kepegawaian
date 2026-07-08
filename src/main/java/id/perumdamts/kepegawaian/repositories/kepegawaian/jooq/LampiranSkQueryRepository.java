package id.perumdamts.kepegawaian.repositories.kepegawaian.jooq;

import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkQuery;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.LampiranSk.LAMPIRAN_SK;

@Repository
@RequiredArgsConstructor
public class LampiranSkQueryRepository {
    private final DSLContext dsl;

    public List<LampiranSkQuery> findByRefAndRefId(EJenisSk jenisSk, Long refId) {
        return dsl.selectFrom(LAMPIRAN_SK)
                .where(LAMPIRAN_SK.REF.eq((byte) jenisSk.ordinal()))
                .and(LAMPIRAN_SK.REF_ID.eq(refId))
                .and(LAMPIRAN_SK.IS_DELETED.eq(false))
                .fetch(this::toQuery);
    }

    public Optional<LampiranSkQuery> getById(Long id) {
        return dsl.selectFrom(LAMPIRAN_SK)
                .where(LAMPIRAN_SK.ID.eq(id))
                .and(LAMPIRAN_SK.IS_DELETED.eq(false))
                .fetchOptional(this::toQuery);
    }

    public Optional<HashedFileInfo> getHashedFileInfoById(Long id) {
        return dsl.select(LAMPIRAN_SK.REF_ID, LAMPIRAN_SK.HASHED_FILE_NAME, LAMPIRAN_SK.FILE_NAME, LAMPIRAN_SK.MIME_TYPE)
                .from(LAMPIRAN_SK)
                .where(LAMPIRAN_SK.ID.eq(id))
                .and(LAMPIRAN_SK.IS_DELETED.eq(false))
                .fetchOptional(record -> new HashedFileInfo(
                        record.get(LAMPIRAN_SK.REF_ID),
                        record.get(LAMPIRAN_SK.HASHED_FILE_NAME),
                        record.get(LAMPIRAN_SK.FILE_NAME),
                        record.get(LAMPIRAN_SK.MIME_TYPE)
                ));
    }

    private LampiranSkQuery toQuery(Record record) {
        Byte refByte = record.get(LAMPIRAN_SK.REF);
        EJenisSk ref = refByte != null ? EJenisSk.values()[refByte.intValue()] : null;
        return new LampiranSkQuery(
                record.get(LAMPIRAN_SK.ID),
                ref,
                record.get(LAMPIRAN_SK.REF_ID),
                record.get(LAMPIRAN_SK.FILE_NAME),
                record.get(LAMPIRAN_SK.MIME_TYPE),
                record.get(LAMPIRAN_SK.NOTES),
                record.get(LAMPIRAN_SK.DISETUJUI),
                record.get(LAMPIRAN_SK.DISETUJUI_OLEH),
                record.get(LAMPIRAN_SK.TANGGAL_DISETUJUI)
        );
    }

    public record HashedFileInfo(Long refId, String hashedFileName, String fileName, String mimeType) {}
}
