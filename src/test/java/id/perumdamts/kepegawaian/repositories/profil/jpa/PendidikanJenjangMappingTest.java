package id.perumdamts.kepegawaian.repositories.profil.jpa;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenjangPendidikanRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Regression for GET /kepegawaian/riwayat/terminasi/calon-pensiun returning 500:
 * {@code Unknown column 'pl1_0.jenjang_pendidikan_id' in 'on clause'}.
 *
 * <p>The {@code Pendidikan} entity mapped its FK as {@code jenjang_pendidikan_id}
 * ({@code @JoinColumn} + {@code @UniqueConstraint}), but the real schema column
 * (V1 baseline, live dev DB, jOOQ codegen) is {@code jenjang_id}. Every JPA
 * query that loads the EAGER {@code jenjangPendidikan} association — INSERT
 * included — generates invalid SQL against the main table. V27 only patched the
 * {@code pendidikan_aud} table, so the main-table drift was never fixed.</p>
 */
@SpringBootTest
@ActiveProfiles("development")
@Transactional
class PendidikanJenjangMappingTest {

    @Autowired private PendidikanRepository pendidikanRepository;
    @Autowired private JenjangPendidikanRepository jenjangRepository;
    @Autowired private BiodataRepository biodataRepository;
    @PersistenceContext private EntityManager em;

    @Test
    void writesAndLoadsPendidikanWithJenjangPendidikanAssociation() {
        JenjangPendidikan jenjang = new JenjangPendidikan();
        jenjang.setNama("S1");
        jenjang.setShortName("Sarjana");
        jenjang.setSeq(1);
        jenjang.setIsStatistik(true);
        // capture return: @Version initialised on the base entities -> save() uses merge()
        JenjangPendidikan savedJenjang = jenjangRepository.saveAndFlush(jenjang);

        Biodata biodata = new Biodata("NIK-REG-JENJANG");
        biodata.setNama("Pegawai Test");
        biodataRepository.saveAndFlush(biodata);

        Pendidikan pendidikan = new Pendidikan();
        pendidikan.setBiodata(biodata);
        pendidikan.setJenjangPendidikan(savedJenjang);
        pendidikan.setTahunMasuk(2020);
        // INSERT must reference jenjang_id (real column), not jenjang_pendidikan_id
        Pendidikan savedPendidikan = pendidikanRepository.saveAndFlush(pendidikan);

        em.flush();
        em.clear();

        // EAGER ManyToOne -> SELECT joins jenjang_pendidikan on pendidikan.jenjang_id
        List<Pendidikan> rows = pendidikanRepository.findAll();
        Pendidikan loaded = rows.stream()
                .filter(p -> p.getId().equals(savedPendidikan.getId()))
                .findFirst()
                .orElseThrow();
        assertNotNull(loaded.getJenjangPendidikan());
        assertEquals("S1", loaded.getJenjangPendidikan().getNama());

        // Native unique-key finder must also reference jenjang_id
        assertDoesNotThrow(() ->
                pendidikanRepository.findAnyByUniqueKey(biodata.getNik(), savedJenjang.getId(), 2020));
    }
}
