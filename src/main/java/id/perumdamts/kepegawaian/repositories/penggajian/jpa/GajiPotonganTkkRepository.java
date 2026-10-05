package id.perumdamts.kepegawaian.repositories.penggajian.jpa;

import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface GajiPotonganTkkRepository extends JpaRepository<GajiPotonganTkk, Long>,
RevisionRepository<GajiPotonganTkk, Long, Integer> {
    Optional<GajiPotonganTkk> findByStatusPegawaiAndLevelIdAndGolonganIsNull(EStatusPegawai statusPegawai, Long levelId);

    Optional<GajiPotonganTkk> findByStatusPegawaiAndGolonganId(EStatusPegawai statusPegawai, Long golonganId);

    Optional<GajiPotonganTkk> findByStatusPegawaiAndLevelIsNullAndGolonganIsNull(EStatusPegawai statusPegawai);

    boolean existsByStatusPegawaiAndLevelIdAndGolonganId(EStatusPegawai statusPegawai, Long levelId, Long golonganId);

    boolean existsByStatusPegawaiAndLevelIdAndGolonganIsNull(EStatusPegawai statusPegawai, Long levelId);

    boolean existsByStatusPegawaiAndGolonganIdAndLevelIsNull(EStatusPegawai statusPegawai, Long golonganId);

    boolean existsByStatusPegawaiAndLevelIsNullAndGolonganIsNull(EStatusPegawai statusPegawai);
}
