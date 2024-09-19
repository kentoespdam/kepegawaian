package id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak;

import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPostRequest;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.kepegawaian.RiwayatKontrakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenericKontrakService {
    private final RiwayatKontrakRepository repository;

    public RiwayatKontrak saveFromPegawai(PegawaiPostRequest request, Pegawai pegawai) {
        RiwayatKontrak entity = RiwayatKontrakPostRequest.toEntity(request, pegawai);
        entity.setIsLatest(true);
        return repository.save(entity);
    }

    public RiwayatKontrak save(RiwayatKontrak entity) {
        RiwayatKontrak save = repository.save(entity);
        if (entity.getIsLatest()) updateLatest(save);
        return save;
    }

    private void updateLatest(RiwayatKontrak entity) {
        Specification<RiwayatKontrak> specification = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("pegawai").get("id"), entity.getPegawai().getId()),
                criteriaBuilder.notEqual(root.get("id"), entity.getId())
        );
        repository.findAll(specification).stream().peek(k -> k.setIsLatest(false)).forEach(repository::save);
    }
}
