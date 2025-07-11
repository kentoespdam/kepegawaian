package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanKlaimPostRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.TanggalHariLibur;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
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

    /**
     * Menyimpan pengajuan klaim cuti pegawai.
     * Proses ini terdiri dari beberapa tahap:
     * - Validasi data pengajuan (tanggal, quota, dll)
     * - Perhitungan jumlah hari cuti
     * - Simpan data pengajuan ke database
     * *
     * Method ini akan mengembalikan objek {@link SavedStatus} yang berisi status
     * simpan data dan pesan error jika terjadi kesalahan.
     *
     * @param request data pengajuan klaim cuti yang akan disimpan.
     * @return objek {@link SavedStatus} yang berisi status simpan data dan pesan error.
     */
    public SavedStatus<?> save(CutiPengajuanKlaimPostRequest request) {
        try {
            // Validate the leave claim request
            CutiPegawai validCutiPegawai = validatePengajuanCutiService.validateKlaim(request);

            // Convert the request into an entity
            CutiPegawai entity = CutiPengajuanKlaimPostRequest.toEntity(validCutiPegawai, request);

            // Set the current PIC as the supervisor
            Jabatan spvSDM = new Jabatan(supervisorSdm);
            entity.setPicSaatIni(spvSDM);

            // Retrieve the list of holidays within the claim period
            List<LocalDate> tanggalLiburList = hariLiburRepository
                    .findByTanggalBetween(request.getListHari().getFirst(), request.getListHari().getLast())
                    .stream().map(TanggalHariLibur::getTanggal).toList();

            // Calculate the list of working days for the claim
            List<LocalDate> tanggalKlaimList = DateHelper.countWorkingDays(request.getListHari(), tanggalLiburList);
            int totalHariCuti = tanggalKlaimList.size();

            // Calculate total remaining leave quota
            int totalRemainingQuota = validCutiPegawai.getRiwayatKuota0() + validCutiPegawai.getRiwayatKuota1();

            // Validate if claimed days are within the remaining quota
            validatePengajuanCutiService.validateMinimalCuti(totalHariCuti, totalRemainingQuota);

            // If quota is insufficient, throw an exception
            if (totalRemainingQuota < totalHariCuti) {
                throw new RuntimeException("Kuota Cuti tidak tersedia! sisa kuota: " + totalRemainingQuota + " hari");
            }

            // Set entity details
            entity.setTanggalMulai(tanggalKlaimList.getFirst());
            entity.setTanggalSelesai(tanggalKlaimList.getLast());
            entity.setKuotaAwal(totalRemainingQuota);
            entity.setKuotaAkhir(totalRemainingQuota - totalHariCuti);
            entity.setJumlahHari(totalHariCuti);
            entity.setJumlahHariKerja(totalHariCuti);

            // Save the entity and claim details
            CutiPegawai save = repository.save(entity);
            List<CutiKlaimDetail> cutiKlaimDetailList = tanggalKlaimList.stream()
                    .map(tanggal -> new CutiKlaimDetail(save, tanggal))
                    .toList();
            cutiKlaimDetailRepository.saveAll(cutiKlaimDetailList);

            return SavedStatus.build(ESaveStatus.SUCCESS, "Pengajuan Klaim Cuti Berhasil disimpan");
        } catch (RuntimeException e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    /**
     * Updates an existing cuti klaim pegawai request.
     *
     * @param id the id of the cuti pegawai to update
     * @param request the updated details of the cuti klaim pegawai
     * @return a SavedStatus object indicating the success or failure of the update
     */
    public SavedStatus<?> update(Long id, CutiPengajuanKlaimPostRequest request) {
        try {
            // Retrieve the existing cuti pegawai
            CutiPegawai cutiPegawai = repository.findByIdAndApprovalCutiStatus(id, EApprovalCutiStatus.PENDING)
                    .orElseThrow(() -> new RuntimeException("Unknown Cuti Pegawai"));

            // Get the list of holidays within the claim period
            List<LocalDate> tanggalLiburList = hariLiburRepository
                    .findByTanggalBetween(request.getListHari().getFirst(), request.getListHari().getLast())
                    .stream().map(TanggalHariLibur::getTanggal).toList();

            // Calculate the list of working days for the claim
            List<LocalDate> tanggalKlaimList = DateHelper.countWorkingDays(request.getListHari(), tanggalLiburList);
            int totalHariCuti = tanggalKlaimList.size();

            // Calculate the total remaining leave quota
            int totalRemainingQuota = cutiPegawai.getRefCuti().getRiwayatKuota0() + cutiPegawai.getRefCuti().getRiwayatKuota1();

            // Validate if claimed days are within the remaining quota
            validatePengajuanCutiService.validateMinimalCuti(totalHariCuti, totalRemainingQuota);

            // If quota is insufficient, throw an exception
            if (totalRemainingQuota < totalHariCuti) {
                throw new RuntimeException("Kuota Cuti tidak tersedia! sisa kuota: " + totalRemainingQuota + " hari");
            }

            // Set the updated details of the cuti pegawai
            cutiPegawai.setAlasan(request.getKeterangan());
            cutiPegawai.setTanggalMulai(tanggalKlaimList.getFirst());
            cutiPegawai.setTanggalSelesai(tanggalKlaimList.getLast());
            cutiPegawai.setKuotaAwal(totalRemainingQuota);
            cutiPegawai.setKuotaAkhir(totalRemainingQuota - totalHariCuti);
            cutiPegawai.setJumlahHari(totalHariCuti);
            cutiPegawai.setJumlahHariKerja(totalHariCuti);

            // Save the updated cuti pegawai
            CutiPegawai save = repository.save(cutiPegawai);

            // Delete all existing cuti klaim details
            List<CutiKlaimDetail> klaimDetails = cutiKlaimDetailRepository.findByRefCuti_id(id);
            cutiKlaimDetailRepository.deleteAll(klaimDetails);

            // Save the updated cuti klaim details
            List<CutiKlaimDetail> cutiKlaimDetailList = tanggalKlaimList.stream()
                    .map(tanggal -> new CutiKlaimDetail(save, tanggal))
                    .toList();
            cutiKlaimDetailRepository.saveAll(cutiKlaimDetailList);

            // Return a SavedStatus object with a success message
            return SavedStatus.build(ESaveStatus.SUCCESS, "Pengajuan Klaim Cuti Berhasil diupdate");
        } catch (RuntimeException e) {
            // Return a SavedStatus object with an error message
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }
}
