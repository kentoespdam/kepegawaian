package id.perumdamts.kepegawaian.services.pegawai.pegawai;

import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PegawaiWriteback {
    private final PegawaiRepository repository;

    public void writebackGolongan(Pegawai pegawai, RiwayatSk riwayatSk) {
        pegawai.setNipam(riwayatSk.getNipam());
        pegawai.setRefSkGolId(riwayatSk.getId());
        pegawai.setTmtGolongan(riwayatSk.getTmtBerlaku());
        pegawai.setMkgTahun(riwayatSk.getMkgTahun());
        pegawai.setMkgBulan(riwayatSk.getMkgBulan());
        if (riwayatSk.getJenisSk().equals(EJenisSk.SK_KENAIKAN_GAJI_BERKALA) ||
                riwayatSk.getJenisSk().equals(EJenisSk.SK_PENYESUAIAN_GAJI) ||
                riwayatSk.getJenisSk().equals(EJenisSk.SK_CAPEG))
            pegawai.setGajiPokok(riwayatSk.getGajiPokok());
        if (riwayatSk.getJenisSk().equals(EJenisSk.SK_CAPEG))
            pegawai.setStatusPegawai(EStatusPegawai.CAPEG);
        if (riwayatSk.getJenisSk().equals(EJenisSk.SK_KENAIKAN_GAJI_BERKALA)){
            pegawai.setRefSkGajiBerkalaId(riwayatSk.getId());
            pegawai.setTmtGajiBerkala(riwayatSk.getTmtBerlaku());
        }

        this.savePegawai(pegawai);
    }

    public void writebackGolonganPensiun(Pegawai pegawai, RiwayatSk riwayatSk, LocalDate tanggalPensiun) {
        pegawai.setTmtPensiun(tanggalPensiun);
        this.writebackGolongan(pegawai, riwayatSk);
    }

    public void writebackJabatan(
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

        this.savePegawai(pegawai);
    }

    public void savePegawai(Pegawai pegawai) {
        repository.save(pegawai);
    }

    public void writebackKontrak(Pegawai pegawai, RiwayatSk riwayatSk, LocalDate tanggalSelesai) {
        pegawai.setTmtPensiun(tanggalSelesai);
        pegawai.setGajiPokok(riwayatSk.getGajiPokok());

        this.savePegawai(pegawai);
    }

}
