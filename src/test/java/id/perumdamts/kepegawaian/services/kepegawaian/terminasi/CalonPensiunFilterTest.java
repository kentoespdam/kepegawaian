package id.perumdamts.kepegawaian.services.kepegawaian.terminasi;

import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiRequest;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.repositories.master.jpa.OrganisasiRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression for GET /kepegawaian/riwayat/terminasi/calon-pensiun:
 * beberapa filter yang dideklarasikan di {@code RiwayatTerminasiRequest}
 * tidak (atau tidak benar) diterapkan oleh {@code getCalonPensiunSpecification()}:
 * <ul>
 *   <li>{@code organisasiId} — dideklarasikan tapi tidak pernah masuk Specification;</li>
 *   <li>{@code nama} — dicocokkan EXACT ({@code addEqual}), bukan partial match
 *       (inkonsisten dengan daftar terminasi utama yang LIKE);</li>
 *   <li>{@code tanggalTerminasi} — ditimpa oleh {@code findPageCalonPensiun()}
 *       dengan jendela tetap now+3bulan, jadi nilai dari user diam-diam dibuang.</li>
 * </ul>
 * Diuji di level repository (spec langsung) dan service. Data ditulis via JPA
 * dan di-rollback otomatis karena test @Transactional.
 */
@SpringBootTest
@ActiveProfiles("development")
@Transactional
class CalonPensiunFilterTest {

    @Autowired private PegawaiRepository pegawaiRepository;
    @Autowired private OrganisasiRepository organisasiRepository;
    @Autowired private BiodataRepository biodataRepository;
    @Autowired private RiwayatTerminasiQueryService queryService;

    private int seq = 0;

    private Organisasi saveOrganisasi(String nama) {
        return organisasiRepository.saveAndFlush(new Organisasi(nama + (++seq)));
    }

    private Pegawai createPegawai(String nama, Organisasi organisasi, LocalDate tmtPensiun) {
        seq++;
        Biodata biodata = new Biodata("NIK-CP-" + seq);
        biodata.setNama(nama);
        biodataRepository.saveAndFlush(biodata);

        Pegawai pegawai = new Pegawai();
        pegawai.setNipam("CP" + System.nanoTime() + "-" + seq);
        pegawai.setBiodata(biodata);
        pegawai.setOrganisasi(organisasi);
        pegawai.setStatusKerja(EStatusKerja.KARYAWAN_AKTIF);
        pegawai.setStatusPegawai(EStatusPegawai.PEGAWAI); // kolom NOT NULL di tabel pegawai
        pegawai.setTmtPensiun(tmtPensiun);
        return pegawaiRepository.saveAndFlush(pegawai);
    }

    @Test
    void filterByOrganisasiId_hanyaMengembalikanPegawaiDariOrganisasiTersebut() {
        Organisasi orgA = saveOrganisasi("ORG-A");
        Organisasi orgB = saveOrganisasi("ORG-B");
        Pegawai pA = createPegawai("PEGAWAI ORG A", orgA, LocalDate.now().plusMonths(1));
        createPegawai("PEGAWAI ORG B", orgB, LocalDate.now().plusMonths(1));

        RiwayatTerminasiRequest request = new RiwayatTerminasiRequest();
        request.setOrganisasiId(orgA.getId());

        Page<Pegawai> page = pegawaiRepository.findAll(
                request.getCalonPensiunSpecification(), PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements(),
                "filter organisasiId harus mengecualikan pegawai dari organisasi lain");
        assertEquals(pA.getId(), page.getContent().getFirst().getId());
    }

    @Test
    void filterByNama_partialMatchCaseInsensitive() {
        Organisasi org = saveOrganisasi("ORG-N");
        Pegawai p = createPegawai("ABDUL AZIZ MIFTAHUDDIN", org, LocalDate.now().plusMonths(1));

        RiwayatTerminasiRequest request = new RiwayatTerminasiRequest();
        request.setNama("abdul");

        Page<Pegawai> page = pegawaiRepository.findAll(
                request.getCalonPensiunSpecification(), PageRequest.of(0, 10));

        assertTrue(page.getContent().stream().anyMatch(pg -> pg.getId().equals(p.getId())),
                "filter nama harus partial match (LIKE) — pegawai dengan nama 'ABDUL AZIZ...' harus muncul untuk input 'abdul'");
    }

    @Test
    void serviceMenghormatiTanggalTerminasiDariRequest() {
        Organisasi org = saveOrganisasi("ORG-T");
        LocalDate tmt = LocalDate.now().plusMonths(6); // di luar jendela default now+3 bulan
        Pegawai p = createPegawai("PEGAWAI TMT JAUH", org, tmt);

        RiwayatTerminasiRequest request = new RiwayatTerminasiRequest();
        request.setTanggalTerminasi(tmt.plusDays(1));

        Page<PegawaiResponse> page = queryService.findPageCalonPensiun(request);

        assertTrue(page.getContent().stream().anyMatch(r -> r.id().equals(p.getId())),
                "tanggalTerminasi dari user harus diperluas, bukan ditimpa jendela now+3bulan");
    }

    @Test
    void filterByTahunPensiun_hanyaPegawaiYangPensiunDiTahunTersebut() {
        Organisasi org = saveOrganisasi("ORG-Y");
        int tahun = LocalDate.now().getYear();
        Pegawai pTahunIni = createPegawai("PEGAWAI PENSIUN TAHUN INI", org, LocalDate.of(tahun, 12, 15));
        createPegawai("PEGAWAI PENSIUN TAHUN DEPAN", org, LocalDate.of(tahun + 1, 3, 15));

        RiwayatTerminasiRequest request = new RiwayatTerminasiRequest();
        request.setOrganisasiId(org.getId()); // scoping: hindari data lama di DB dev
        request.setTahunPensiun(tahun);

        Page<Pegawai> page = pegawaiRepository.findAll(
                request.getCalonPensiunSpecification(), PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements(),
                "filter tahunPensiun harus membatasi ke pegawai yang TMT pensiun-nya di tahun tersebut");
        assertEquals(pTahunIni.getId(), page.getContent().getFirst().getId());
    }

    @Test
    void boundaryTanggalTerminasiInklusif_tmtSamaDenganTanggalTerminasi() {
        Organisasi org = saveOrganisasi("ORG-BD");
        LocalDate tmt = LocalDate.now().plusMonths(6);
        Pegawai p = createPegawai("PEGAWAI TMT TEPAT DI BATAS", org, tmt);

        RiwayatTerminasiRequest request = new RiwayatTerminasiRequest();
        request.setOrganisasiId(org.getId()); // scoping: hindari data lama di DB dev
        request.setTanggalTerminasi(tmt); // sama persis dengan tmtPensiun

        Page<Pegawai> page = pegawaiRepository.findAll(
                request.getCalonPensiunSpecification(), PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements(),
                "batas atas tanggalTerminasi harus inklusif — pegawai dengan tmtPensiun == tanggalTerminasi ikut masuk");
        assertEquals(p.getId(), page.getContent().getFirst().getId());
    }

    @Test
    void serviceTahunPensiunMemperluasJendelaDefault_keSeluruhTahun() {
        Organisasi org = saveOrganisasi("ORG-W");
        int tahun = LocalDate.now().getYear();
        // 15 Desember tahun ini berada DI LUAR jendela default now+3 bulan,
        // tetapi masih dalam tahun yang difilter → harus tetap muncul.
        LocalDate tmt = LocalDate.of(tahun, 12, 15);
        Pegawai p = createPegawai("PEGAWAI PENSIUN AKHIR TAHUN", org, tmt);

        RiwayatTerminasiRequest request = new RiwayatTerminasiRequest();
        request.setTahunPensiun(tahun);

        Page<PegawaiResponse> page = queryService.findPageCalonPensiun(request);

        assertTrue(page.getContent().stream().anyMatch(r -> r.id().equals(p.getId())),
                "filter tahunPensiun harus memperluas jendela ke seluruh tahun, bukan terpotong now+3 bulan");
    }
}
