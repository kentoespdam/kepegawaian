package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.services.setupMaster.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/setup-master")
@RequiredArgsConstructor
public class SetupMasterController {
    private final SetupLevel setupLevel;
    private final SetupOrganisasi setupOrganisasi;
    private final SetupGolongan setupGolongan;
    private final SetupGrade setupGrade;
    private final SetupJabatan setupJabatan;
    private final SetupProfesi setupProfesi;
    private final SetupJenisKeahlian setupJenisKeahlian;
    private final SetupJenisKitas setupJenisKitas;
    private final SetupJenisPelatihan setupJenisPelatihan;
    private final SetupJenjangPendidikan setupJenjangPendidikan;
    private final SetupJenisSp setupJenisSp;
    private final SetupSanksi setupSanksi;
    private final SetupAlasanBerhenti setupAlasanBerhenti;
    private final SetupDasarGaji setupDasarGaji;
    private final SetupDetailDasarGaji setupDetailDasarGaji;
    private final SetupPendapatanNonPajak setupPendapatanNonPajak;
    private final SetupGajiProfil setupGajiProfil;
    private final SetupGajiKomponen setupGajiKomponen;
    private final SetupRumahDinas setupRumahDinas;
    private final SetupGajiTunjangan setupGajiTunjangan;
    private final SetupGajiPotonganTkk setupGajiPotonganTkk;
    private final SetupGajiParameterSetting setupGajiParameterSetting;
    private final SetupPrefRole setupPrefRole;
    private final SetupCutiJenis setupCutiJenis;

    private SetupMaster[] getServicesInOrder() {
        return new SetupMaster[]{
                setupLevel, setupOrganisasi, setupGolongan, setupGrade, setupJabatan,
                setupProfesi, setupJenisKeahlian, setupJenisKitas, setupJenisPelatihan,
                setupJenjangPendidikan, setupJenisSp, setupSanksi, setupAlasanBerhenti,
                setupDasarGaji, setupDetailDasarGaji, setupPendapatanNonPajak,
                setupGajiProfil, setupGajiKomponen, setupRumahDinas, setupGajiTunjangan,
                setupGajiPotonganTkk, setupGajiParameterSetting, setupPrefRole, setupCutiJenis
        };
    }

    @GetMapping
    public ResponseEntity<?> initialData() {
        List<String> errors = new ArrayList<>();
        SetupMaster[] servicesInOrder = getServicesInOrder();
        for (SetupMaster service : servicesInOrder) {
            try {
                service.insertBatch();
                log.info("✅ Success: {}", service.getClass().getSimpleName());
            } catch (Exception e) {
                String error = String.format("%s: %s",
                        service.getClass().getSimpleName(), e.getMessage());
                errors.add(error);
                log.error("❌ {}", error);

            }
        }

        return errors.isEmpty()
                ? ResponseEntity.ok("All master data initialized successfully")
                : ResponseEntity.status(500)
                .body("Partial success. Errors: " + String.join("; ", errors));
    }

}
