package id.perumdamts.kepegawaian.mapper.profil.biodata;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataDetail;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasQuery;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanQuery;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EGolonganDarah;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.jooq.enums.BiodataGolonganDarah;
import org.jooq.Record;

import java.time.LocalDate;
import java.util.List;

public final class BiodataDetailJooqMapper {

    private BiodataDetailJooqMapper() {
    }

    public static BiodataDetail map(Record record,
                             List<PendidikanQuery> pendidikan,
                             List<KartuIdentitasQuery> kartuIdentitas) {
        Byte jk = record.get("jenis_kelamin", Byte.class);
        Byte ag = record.get("agama", Byte.class);
        Byte sk = record.get("status_kawin", Byte.class);
        BiodataGolonganDarah gd = record.get("golongan_darah", BiodataGolonganDarah.class);

        return new BiodataDetail(
                record.get("nik", String.class),
                record.get("nama", String.class),
                jk != null ? EJenisKelamin.values()[jk] : null,
                record.get("tempat_lahir", String.class),
                record.get("tanggal_lahir", LocalDate.class),
                record.get("alamat", String.class),
                record.get("telp", String.class),
                ag != null ? EAgama.values()[ag] : null,
                record.get("ibu_kandung", String.class),
                record.get("pendidikan_id", Long.class),
                gd != null ? EGolonganDarah.valueOf(gd.name()) : null,
                sk != null ? EStatusKawin.values()[sk] : null,
                record.get("foto_profil", String.class),
                record.get("notes", String.class),
                record.get("is_pegawai", Boolean.class),
                pendidikan,
                kartuIdentitas
        );
    }
}
