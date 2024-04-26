package id.perumdamts.kepegawaian.controllers.master;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.repositories.master.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/setup-master")
public class SetupMasterController {
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private StatusPegawaiRepository statusPegawaiRepository;
    @Autowired
    private GolonganRepository golonganRepository;
    @Autowired
    private ProfesiRepository profesiRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private JabatanRepository jabatanRepository;
    @Autowired
    private OrganisasiRepository organisasiRepository;
    @Autowired
    private JenisKeahlianRepository jenisKeahlianRepository;
    @Autowired
    private JenisKitasRepository jenisKitasRepository;
    @Autowired
    private JenisPelatihanRepository jenisPelatihanRepository;
    @Autowired
    private JenjangPendidikanRepository jenjangPendidikanRepository;
    @Autowired
    private JenisLampiranProfilRepository jenisLampiranProfilRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping
    public ResponseEntity<?> initialData() {
        try {
            insertDataLevel();
            insertDataStatusPegawai();
            insertDataGolongan();
            insertDataProfesi();
            insertDataGrade();
            insertDataOrganisasi();
            insertDataJabatan();
            insertDataJenisKeahlian();
            insertDataJenisKitas();
            insertDataJenisPelatihan();
            insertDataJenjangPendidikan();
            insertDataJenisLampiranProfil();
            return ResponseEntity.ok().build();
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private void insertDataLevel() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonLevel = "[{\"id\":1,\"nama\":\"DEWAS\"},{\"id\":2,\"nama\":\"DIRUT\"},{\"id\":3,\"nama\":\"DIRTEK\"},{\"id\":4,\"nama\":\"DIRUM\"},{\"id\":5,\"nama\":\"MANAJER\"},{\"id\":6,\"nama\":\"SUPERVISOR\"},{\"id\":7,\"nama\":\"STAFF\"}]";
        List<Level> levelList = mapper.readValue(jsonLevel, new TypeReference<>() {
        });
        levelRepository.saveAll(levelList);
    }

    private void insertDataStatusPegawai() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonStatusPegawai = "[{\"id\":1,\"nama\":\"KONTRAK\"},{\"id\":2,\"nama\":\"CAPEG\"},{\"id\":3,\"nama\":\"PEGAWAI\"},{\"id\":4,\"nama\":\"HONORER TETAP\"}]";
        List<StatusPegawai> statusPegawaiList = mapper.readValue(jsonStatusPegawai, new TypeReference<>() {
        });
        statusPegawaiRepository.saveAll(statusPegawaiList);
    }

    private void insertDataGolongan() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonGolongan = "[{\"id\":1,\"golongan\":\"A.1\",\"pangkat\":\"Pegawai Dasar Muda\"},{\"id\":2,\"golongan\":\"A.2\",\"pangkat\":\"Pegawai Dasar Muda Tk.I\"},{\"id\":3,\"golongan\":\"A.3\",\"pangkat\":\"Pegawai Dasar\"},{\"id\":4,\"golongan\":\"A.4\",\"pangkat\":\"Pegawai Dasar Tk.I\"}]";
        List<Golongan> golonganList = mapper.readValue(jsonGolongan, new TypeReference<>() {
        });
        golonganRepository.saveAll(golonganList);
    }

    private void insertDataProfesi() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonProfesi = "[{\"id\":1,\"level\":{\"id\":5,\"nama\":\"MANAJER\"},\"nama\":\"Manajer Perencanaan & Pengembangan\"},{\"id\":2,\"level\":{\"id\":5,\"nama\":\"MANAJER\"},\"nama\":\"Manajer Produksi & Distribusi 1\"},{\"id\":3,\"level\":{\"id\":5,\"nama\":\"MANAJER\"},\"nama\":\"Manajer Pengendalian Teknik\"},{\"id\":4,\"level\":{\"id\":5,\"nama\":\"MANAJER\"},\"nama\":\"Manajer Keuangan\"}]";
        List<Profesi> profesiList = mapper.readValue(jsonProfesi, new TypeReference<>() {
        });
        profesiRepository.saveAll(profesiList);
    }

    private void insertDataGrade() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonGrade = "[{\"id\":1,\"level\":{\"id\":5,\"nama\":\"MANAJER\"},\"grade\":1,\"tukin\":300000},{\"id\":2,\"level\":{\"id\":5,\"nama\":\"MANAJER\"},\"grade\":2,\"tukin\":3150000},{\"id\":3,\"level\":{\"id\":5,\"nama\":\"MANAJER\"},\"grade\":3,\"tukin\":3300000},{\"id\":4,\"level\":{\"id\":6,\"nama\":\"SUPERVISOR\"},\"grade\":1,\"tukin\":1930500},{\"id\":5,\"level\":{\"id\":6,\"nama\":\"SUPERVISOR\"},\"grade\":2,\"tukin\":2216500}]";
        List<Grade> gradeList = mapper.readValue(jsonGrade, new TypeReference<>() {
        });
        gradeRepository.saveAll(gradeList);
    }

    private void insertDataJabatan() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonJabatan = "[{\"id\":1,\"jabatan\":null,\"organisasi\":{\"id\":1,\"organisasi\":null,\"levelOrg\":1,\"nama\":\"DEWAN PENGAWAS\"},\"level\":{\"id\":1,\"nama\":\"DEWAS\"},\"nama\":\"Kepala Dewan Pengawas\"},{\"id\":2,\"jabatan\":{\"id\":1,\"jabatan\":null,\"organisasi\":{\"id\":1,\"organisasi\":null,\"levelOrg\":1,\"nama\":\"DEWAN PENGAWAS\"},\"level\":{\"id\":1,\"nama\":\"DEWAS\"},\"nama\":\"Kepala Dewan Pengawas\"},\"organisasi\":{\"id\":2,\"organisasi\":{\"id\":1,\"organisasi\":null,\"levelOrg\":1,\"nama\":\"DEWAN PENGAWAS\"},\"levelOrg\":2,\"nama\":\"DIREKTORAT UTAMA\"},\"level\":{\"id\":2,\"nama\":\"DIRUT\"},\"nama\":\"Direktur Utama\"},{\"id\":3,\"jabatan\":{\"id\":2,\"jabatan\":null,\"organisasi\":{\"id\":2,\"organisasi\":{\"id\":1,\"organisasi\":null,\"levelOrg\":1,\"nama\":\"DEWAN PENGAWAS\"},\"levelOrg\":2,\"nama\":\"DIREKTORAT UTAMA\"},\"level\":{\"id\":2,\"nama\":\"DIRUT\"},\"nama\":\"Direktur Utama\"},\"organisasi\":{\"id\":3,\"organisasi\":{\"id\":2,\"organisasi\":null,\"levelOrg\":2,\"nama\":\"DIREKTORAT UTAMA\"},\"levelOrg\":3,\"nama\":\"DIREKTORAT TEKNIK\"},\"level\":{\"id\":3,\"nama\":\"DIRTEK\"},\"nama\":\"Direktur Teknik\"}]";
        List<Jabatan> jabatanList = mapper.readValue(jsonJabatan, new TypeReference<>() {
        });
        jabatanRepository.saveAll(jabatanList);
    }

    private void insertDataOrganisasi() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonOrganisasi = "[{\"id\":1,\"organisasi\":null,\"levelOrg\":1,\"nama\":\"DEWAN PENGAWAS\"},{\"id\":2,\"organisasi\":{\"id\":1,\"organisasi\":null,\"levelOrg\":1,\"nama\":\"DEWAN PENGAWAS\"},\"levelOrg\":2,\"nama\":\"DIREKTORAT UTAMA\"},{\"id\":3,\"organisasi\":{\"id\":2,\"organisasi\":null,\"levelOrg\":2,\"nama\":\"DIREKTORAT UTAMA\"},\"levelOrg\":3,\"nama\":\"DIREKTORAT TEKNIK\"},{\"id\":4,\"organisasi\":{\"id\":2,\"organisasi\":null,\"levelOrg\":2,\"nama\":\"DIREKTORAT UTAMA\"},\"levelOrg\":3,\"nama\":\"DIREKTORAT ADMIN & KEUANGAN\"}]";
        List<Organisasi> organisasiList = mapper.readValue(jsonOrganisasi, new TypeReference<>() {
        });
        System.out.println(organisasiList);
        organisasiRepository.saveAll(organisasiList);
    }

    private void insertDataJenisKeahlian() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonData = "[{\"nama\":\"Pemrograman\"},{\"nama\":\"Desain Grafis\"},{\"nama\":\"Desain Grafis\"},{\"nama\":\"Bhs. Inggris\"},{\"nama\":\"Teknisi Komputer\"},{\"nama\":\"Ahli MAM Muda\"},{\"nama\":\"Ahli MAM Madya\"},{\"nama\":\"Ahli MAM Utama\"},{\"nama\":\"Ahli Akuntansi\"},{\"nama\":\"Ahli Pengadaan\"},{\"nama\":\"Assessor\"}]";
        List<JenisKeahlian> jenisKeahlianList = mapper.readValue(jsonData, new TypeReference<>() {
        });
        jenisKeahlianRepository.saveAll(jenisKeahlianList);
    }

    private void insertDataJenisKitas() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonData = "[{\"nama\":\"KTP\"},{\"nama\":\"Jamsostek\"},{\"nama\":\"Desain Grafis\"},{\"nama\":\"ASKES/BPJS\"},{\"nama\":\"NPWP\"},{\"nama\":\"SIM\"},{\"nama\":\"Dapenma\"},{\"nama\":\"JPn\"},{\"nama\":\"Yakan\"},{\"nama\":\"Korpri\"},{\"nama\":\"ID Card\"}]";
        List<JenisKitas> jenisKitasList = mapper.readValue(jsonData, new TypeReference<>() {
        });
        jenisKitasRepository.saveAll(jenisKitasList);
    }

    private void insertDataJenisPelatihan() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonData = "[{\"nama\":\"Administrasi\"},{\"nama\":\"Keuangan \"},{\"nama\":\"Pelayanan \"},{\"nama\":\"IT\"},{\"nama\":\"Perpipaan\"},{\"nama\":\"Listrik & Perpompaan\"},{\"nama\":\"Pengolahan\"},{\"nama\":\"SPAM\"}]";
        List<JenisPelatihan> jenisPelatihanList = mapper.readValue(jsonData, new TypeReference<>() {
        });
        jenisPelatihanRepository.saveAll(jenisPelatihanList);
    }

    private void insertDataJenjangPendidikan() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonData = "[{\"nama\":\"SD - Sederajat\",\"seq\":1},{\"nama\":\"SMP - Sederajat\",\"seq\":2},{\"nama\":\"SMA - Sederajat\",\"seq\":3},{\"nama\":\"Diploma 1\",\"seq\":4},{\"nama\":\"Diploma 2\",\"seq\":5},{\"nama\":\"Diploma 3\",\"seq\":6},{\"nama\":\"S1\",\"seq\":7},{\"nama\":\"S2\",\"seq\":8},{\"nama\":\"S3\",\"seq\":9}]";
        List<JenjangPendidikan> jenjangPendidikanList = mapper.readValue(jsonData, new TypeReference<>() {
        });
        jenjangPendidikanRepository.saveAll(jenjangPendidikanList);
    }

    private void insertDataJenisLampiranProfil() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonData = "[{\"nama\":\"PROFIL_KELUARGA\"},{\"nama\":\"PROFIL_PENDIDIKAN\"},{\"nama\":\"PROFIL_PELATIHAN\"},{\"nama\":\"PROFIL_KEAHLIAN\"},{\"nama\":\"KITAS\"},{\"nama\":\"PROFIL_PENGALAMAN_KERJA\"}]";
        List<JenisLampiranProfil> jenisLampiranProfilList = mapper.readValue(jsonData, new TypeReference<>() {
        });
        jenisLampiranProfilRepository.saveAll(jenisLampiranProfilList);
    }
}
