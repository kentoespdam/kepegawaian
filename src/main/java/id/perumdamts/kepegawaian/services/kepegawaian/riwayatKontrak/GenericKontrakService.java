package id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak;

import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPutRequest;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.kepegawaian.RiwayatKontrakRepository;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.GenericSkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenericKontrakService {
    private final RiwayatKontrakRepository repository;
    private final GenericSkService genericSkService;
    private final GolonganRepository golonganRepository;

    public RiwayatKontrak saveFromPegawai(PegawaiPostRequest request, Pegawai pegawai) {
        genericSkService.saveSkKontrakFromPegawai(request, pegawai);
        RiwayatKontrak entity = RiwayatKontrakPostRequest.toEntity(request, pegawai);

        entity.setIsLatest(true);
        return repository.save(entity);
    }

    public RiwayatKontrak save(RiwayatKontrak entity) {
        RiwayatKontrak save = repository.save(entity);
        if (entity.getIsLatest()) updateLatest(save);
        return save;
    }

    public RiwayatKontrak save(RiwayatKontrakPostRequest request, Pegawai pegawai) {
        RiwayatKontrak entity = RiwayatKontrakPostRequest.toEntity(request, pegawai);
        switch (request.getJenisKontrak()) {
            case PERPANJANGAN:
                genericSkService.saveKontrak(request, pegawai);
                break;
            case PENGANGKATAN:
                Golongan golongan = golonganRepository.findById(request.getGolonganId()).orElseThrow(() -> new RuntimeException("Unknown Golongan"));
                genericSkService.saveKontrakToCapeg(request, pegawai, golongan);
                break;
        }

        return save(entity);
    }

    public RiwayatKontrak update(RiwayatKontrak riwayatKontrak, RiwayatKontrakPutRequest request, Pegawai pegawai) {
        RiwayatKontrak entity = RiwayatKontrakPutRequest.toEntity(riwayatKontrak, request, pegawai);
        switch (request.getJenisKontrak()) {
            case PERPANJANGAN:
                genericSkService.saveKontrak(request, pegawai);
                break;
            case PENGANGKATAN:
                genericSkService.saveKontrakToCapeg(request, pegawai, pegawai.getGolongan());
                break;
        }
        return save(entity);
    }

    public void delete(RiwayatKontrak riwayatKontrak){
        repository.save(riwayatKontrak);
        genericSkService.delete(riwayatKontrak);
    }

    private void updateLatest(RiwayatKontrak entity) {
        Specification<RiwayatKontrak> specification = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("pegawai").get("id"), entity.getPegawai().getId()),
                criteriaBuilder.notEqual(root.get("id"), entity.getId())
        );
        repository.findAll(specification).stream().peek(k -> k.setIsLatest(false)).forEach(repository::save);
    }
}
