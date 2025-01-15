package id.perumdamts.kepegawaian.controllers.master;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.perumdamts.kepegawaian.services.setupMaster.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/setup-master")
@RequiredArgsConstructor
public class SetupMasterController {
    private final SetupOrganisasi setupOrganisasi;
    private final SetupLevel setupLevel;
    private final SetupGolongan setupGolongan;
    private final SetupGrade setupGrade;
    private final SetupProfesi setupProfesi;
    private final SetupJabatan setupJabatan;
    private final SetupJenisKeahlian setupJenisKeahlian;
    private final SetupJenisKitas setupJenisKitas;
    private final SetupJenisPelatihan setupJenisPelatihan;
    private final SetupJenjangPendidikan setupJenjangPendidikan;
    private final SetupAlasanBerhenti setupAlasanBerhenti;
    private final SetupDasarGaji setupDasarGaji;
    private final SetupDetailDasarGaji setupDetailDasarGaji;
    private final SetupPendapatanNonPajak setupPendapatanNonPajak;
    private final SetupGajiProfil setupGajiProfil;
    private final SetupGajiKomponen setupGajiKomponen;
    private final SetupRumahDinas setupRumahDinas;

    @GetMapping
    public ResponseEntity<?> initialData() {
        try {
            setupLevel.insertBatch();
            setupOrganisasi.insertBatch();
            setupJabatan.insertBatch();
            setupGolongan.insertBatch();
            setupDasarGaji.insertBatch();
            setupGrade.insertBatch();
            setupProfesi.insertBatch();
            setupJenisKeahlian.insertBatch();
            setupJenisKitas.insertBatch();
            setupJenisPelatihan.insertBatch();
            setupJenjangPendidikan.insertBatch();
            setupAlasanBerhenti.insertBatch();
            setupDetailDasarGaji.insertBatch();
            setupPendapatanNonPajak.insertBatch();
            setupGajiProfil.insertBatch();
            setupGajiKomponen.insertBatch();
            setupRumahDinas.insertBatch();
            return ResponseEntity.ok().build();
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}
