package id.perumdamts.kepegawaian.services.master.sanksi;

import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiPostRequest;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiPutRequest;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.entities.master.Sanksi;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.master.sanksi.SanksiMapper;
import id.perumdamts.kepegawaian.repositories.master.JenisSpRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.SanksiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SanksiCommandService {
    private final SanksiRepository repository;
    private final JenisSpRepository jenisSpRepository;

    @Transactional
    public Sanksi create(SanksiPostRequest request) {
        JenisSp jenisSp = findJenisSp(request.getJenisSpId());
        Optional<Sanksi> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                Sanksi revived = existing.get();
                revived.setIsDeleted(false);
                SanksiMapper.updateEntity(revived, request, jenisSp);
                return repository.save(revived);
            }
            throw new ConflictException("Sanksi already exists");
        }
        return repository.save(SanksiMapper.toEntity(request, jenisSp));
    }

    @Transactional
    public Sanksi update(Long id, SanksiPutRequest request) {
        Sanksi existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sanksi not found"));
        JenisSp jenisSp = findJenisSp(request.getJenisSpId());
        Optional<Sanksi> duplicate = repository.findOne(request.getSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("Sanksi already exists");
        }
        SanksiMapper.updateEntity(existing, request, jenisSp);
        return repository.save(existing);
    }

    @Transactional
    public Sanksi updateJenisSp(Long id, Long jenisSpId) {
        Sanksi existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sanksi not found"));
        JenisSp jenisSp = findJenisSp(jenisSpId);
        existing.setJenisSp(jenisSp);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Sanksi existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sanksi not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
    }

    private JenisSp findJenisSp(Long id) {
        if (id == null) return null;
        return jenisSpRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Jenis SP not found"));
    }
}
