package id.perumdamts.kepegawaian.mapper.master.hariLibur;

import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburListResponse;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburQuery;
import id.perumdamts.kepegawaian.entities.commons.EJenisLibur;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.HariLibur.HARI_LIBUR;

public final class HariLiburJooqMapper {
    private HariLiburJooqMapper() {}

    public static HariLiburQuery toQuery(Record record) {
        return new HariLiburQuery(
                record.get(HARI_LIBUR.ID),
                record.get(HARI_LIBUR.TANGGAL),
                jenisLiburByteToString(record.get(HARI_LIBUR.JENIS_LIBUR)),
                record.get(HARI_LIBUR.NOTES)
        );
    }

    public static HariLiburListResponse toListResponse(Record record) {
        return new HariLiburListResponse(
                record.get(HARI_LIBUR.ID),
                record.get(HARI_LIBUR.TANGGAL),
                jenisLiburByteToString(record.get(HARI_LIBUR.JENIS_LIBUR))
        );
    }

    private static String jenisLiburByteToString(Byte ordinal) {
        if (ordinal == null) return null;
        for (EJenisLibur e : EJenisLibur.values()) {
            if (e.ordinal() == ordinal)
                return e.getValue();
        }
        return null;
    }
}
