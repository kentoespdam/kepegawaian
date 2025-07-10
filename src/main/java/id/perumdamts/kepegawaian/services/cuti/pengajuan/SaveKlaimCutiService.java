package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanKlaimRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.TanggalHariLibur;
import id.perumdamts.kepegawaian.entities.cuti.CutiKlaimDetail;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.helpers.DateHelper;
import id.perumdamts.kepegawaian.repositories.cuti.CutiKlaimDetailRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.HariLiburRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaveKlaimCutiService {
    private final CutiPegawaiRepository repository;
    private final ValidatePengajuanCutiService validatePengajuanCutiService;
    private final HariLiburRepository hariLiburRepository;
    private final CutiKlaimDetailRepository cutiKlaimDetailRepository;

    @Value("${custom.jabatan.supervisorSdm}")
    private Long supervisorSdm;

    public SavedStatus<?> save(CutiPengajuanKlaimRequest request) {
        try {
            // validate
            CutiPegawai validCutiPegawai = validatePengajuanCutiService.validateKlaim(request);
            CutiPegawai entity = CutiPengajuanKlaimRequest.toEntity(validCutiPegawai, request);
            Jabatan spvSDM = new Jabatan(supervisorSdm);
            entity.setPicSaatIni(spvSDM);
            List<LocalDate> tanggalLiburList = hariLiburRepository
                    .findByTanggalBetween(request.getListHari().getFirst(), request.getListHari().getLast())
                    .stream().map(TanggalHariLibur::getTanggal).toList();
            List<LocalDate> tanggalKlaimList = DateHelper.countWorkingDays(request.getListHari(), tanggalLiburList);
            int totalHariCuti = tanggalKlaimList.size();
            int totalRemainingQuota = validCutiPegawai.getRiwayatKuota0() + validCutiPegawai.getRiwayatKuota1();
            validatePengajuanCutiService.validateMinimalCuti(totalHariCuti, totalRemainingQuota);

            // jika kuota tidak mencukupi maka batalkan
            if (totalRemainingQuota < totalHariCuti) {
                throw new RuntimeException("Kuota Cuti tidak tersedia! sisa kuota: " + totalRemainingQuota + " hari");
            }

            entity.setKuotaAwal(totalRemainingQuota);
            entity.setKuotaAkhir(totalRemainingQuota - totalHariCuti);
            entity.setJumlahHari(totalHariCuti);
            entity.setJumlahHariKerja(totalHariCuti);

            CutiPegawai save = repository.save(entity);
            cutiKlaimDetailRepository.saveAll(tanggalKlaimList.stream()
                    .map(tanggal -> new CutiKlaimDetail(save, tanggal))
                    .toList());
            return SavedStatus.build(ESaveStatus.SUCCESS, "Pengajuan Klaim Cuti Berhasil disimpan");
        } catch (RuntimeException e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }
}
