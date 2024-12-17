package id.perumdamts.kepegawaian.services.pegawai;

import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenericPegawaiService {
    private final PegawaiRepository repository;

    public void updateGolongan(Pegawai pegawai, RiwayatSk riwayatSk) {
        pegawai.setNipam(riwayatSk.getNipam());
        pegawai.setRefSkGolId(riwayatSk.getId());
        pegawai.setTmtGolongan(riwayatSk.getTmtBerlaku());
        pegawai.setMkgTahun(riwayatSk.getMkgTahun());
        pegawai.setMkgBulan(riwayatSk.getMkgBulan());
        if (riwayatSk.getJenisSk().equals(EJenisSk.SK_KENAIKAN_GAJI_BERKALA) ||
                riwayatSk.getJenisSk().equals(EJenisSk.SK_PENYESUAIAN_GAJI) ||
                riwayatSk.getJenisSk().equals(EJenisSk.SK_CAPEG))
            pegawai.setGajiPokok(riwayatSk.getGajiPokok());
        if(riwayatSk.getJenisSk().equals(EJenisSk.SK_CAPEG))
            pegawai.setStatusPegawai(EStatusPegawai.CAPEG);

        this.updatePegawai(pegawai);
    }

    public void updateJabatan(
            Pegawai pegawai,
            RiwayatSk riwayatSk,
            Organisasi organisasiBaru,
            Jabatan jabatanBaru,
            Profesi profesiBaru
    ) {
        if (riwayatSk.getJenisSk().equals(EJenisSk.SK_KENAIKAN_PANGKAT_GOLONGAN)) {
            pegawai.setRefSkJabatanId(riwayatSk.getId());
            pegawai.setTmtJabatan(riwayatSk.getTmtBerlaku());
        }
        if (riwayatSk.getJenisSk().equals(EJenisSk.SK_MUTASI)) {
            pegawai.setRefSkMutasiId(riwayatSk.getId());
            pegawai.setTmtMutasi(riwayatSk.getTmtBerlaku());
        }

        pegawai.setOrganisasi(organisasiBaru);
        pegawai.setJabatan(jabatanBaru);
        pegawai.setProfesi(profesiBaru);

        this.updatePegawai(pegawai);
    }

    public void updatePegawai(Pegawai pegawai) {
        repository.save(pegawai);
    }

    public void updateKontrak(Pegawai pegawai, RiwayatSk riwayatSk) {
        pegawai.setGajiPokok(riwayatSk.getGajiPokok());

        this.updatePegawai(pegawai);
    }

}
