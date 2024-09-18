package id.perumdamts.kepegawaian.services.pegawai;

import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenericPegawaiService {
    private final PegawaiRepository repository;

    public void updateGolongan(Pegawai pegawai, RiwayatSk riwayatSk, Golongan golongan) {
        pegawai.setRefSkGolId(golongan.getId());
        pegawai.setTmtGolongan(riwayatSk.getTmtBerlaku());
        pegawai.setMkgTahun(riwayatSk.getMkgTahun());
        pegawai.setMkgBulan(riwayatSk.getMkgBulan());
        if (riwayatSk.getJenisSk().equals(EJenisSk.SK_KENAIKAN_GAJI_BERKALA) ||
                riwayatSk.getJenisSk().equals(EJenisSk.SK_PENYESUAIAN_GAJI))
            pegawai.setGajiPokok(riwayatSk.getGajiPokok());

        this.updatePegawai(pegawai);
    }

    public void updateJabatan(Pegawai pegawai, RiwayatSk riwayatSk) {
        pegawai.setRefSkJabatanId(riwayatSk.getId());
        pegawai.setTmtJabatan(riwayatSk.getTmtBerlaku());
        this.updatePegawai(pegawai);
    }

    public void updatePegawai(Pegawai pegawai) {
        repository.save(pegawai);
    }
}
