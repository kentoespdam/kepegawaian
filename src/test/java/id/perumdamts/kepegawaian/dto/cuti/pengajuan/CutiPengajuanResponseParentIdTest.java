package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisMiniResponse;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Regression (FE-CONTRACT-cuti-jenis-mini-parentid): {@code CutiJenisMiniResponse} kini
 * membawa {@code parentId}. Jalur entity: {@code subJenisCuti.parentId} harus sama dengan
 * {@code jenisCuti.id} (parent sub-jenis = jenis cuti), dan jenis root tanpa parent harus
 * membawa {@code parentId = null} — bukan NPE.
 */
class CutiPengajuanResponseParentIdTest {

    private CutiJenis jenis(Long id, String nama, CutiJenis parent) {
        CutiJenis jenis = new CutiJenis(id);
        jenis.setNama(nama);
        jenis.setParent(parent);
        return jenis;
    }

    private CutiPegawai pengajuan(CutiJenis jenisCuti, CutiJenis subJenisCuti) {
        Pegawai pegawai = new Pegawai();
        pegawai.setId(9L);
        pegawai.setNipam("830100446");
        Biodata biodata = new Biodata("3273012345678901");
        biodata.setNama("Pegawai Test");
        pegawai.setBiodata(biodata);
        pegawai.setOrganisasi(new Organisasi());
        Jabatan jabatan = new Jabatan();
        jabatan.setLevel(new Level());
        pegawai.setJabatan(jabatan);

        CutiPegawai entity = new CutiPegawai();
        entity.setId(100L);
        entity.setPegawai(pegawai);
        entity.setCreatedAt(LocalDateTime.of(2026, 8, 1, 9, 0));
        entity.setJenisCuti(jenisCuti);
        entity.setSubJenisCuti(subJenisCuti);
        return entity;
    }

    @Test
    void subJenisCutiMembawaParentIdJenisCuti() {
        CutiJenis root = jenis(1L, "Cuti Tahunan", null);
        CutiJenis sub = jenis(2L, "Cuti Sakit", root);

        CutiPengajuanResponse response = CutiPengajuanResponse.from(pengajuan(root, sub));

        assertEquals(2L, response.subJenisCuti().id());
        assertEquals(1L, response.jenisCuti().id());
        assertEquals(response.jenisCuti().id(), response.subJenisCuti().parentId(),
                "subJenisCuti.parentId harus sama dengan jenisCuti.id (jalur entity)");
        assertNull(response.jenisCuti().parentId(),
                "jenisCuti root tanpa parent harus membawa parentId null");
    }

    @Test
    void subJenisTanpaParentMembawaParentIdNull() {
        CutiJenis sub = jenis(2L, "Cuti Sakit", null);

        CutiJenisMiniResponse mini = CutiJenisMiniResponse.from(sub);

        assertNull(mini.parentId(),
                "parentId harus null saat entity.getParent() null (bukan NPE)");
    }
}
