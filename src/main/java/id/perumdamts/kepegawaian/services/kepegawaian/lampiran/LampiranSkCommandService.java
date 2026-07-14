package id.perumdamts.kepegawaian.services.kepegawaian.lampiran;

import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkAcceptRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.LampiranSk;
import id.perumdamts.kepegawaian.exceptions.BadRequestException;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.kepegawaian.lampiran.LampiranSkMapper;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.LampiranSkRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LampiranSkCommandService {
    private final LampiranSkRepository repository;
    private final FileUploadUtil fileUploadUtil;

    @Transactional(rollbackFor = Exception.class)
    public LampiranSk addLampiran(LampiranSkPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) {
            throw new ConflictException("Lampiran SK sudah ada");
        }
        UploadResultUtil uploadedFile = fileUploadUtil.uploadFileSp(request.getFileName(), request.getRef(), String.valueOf(request.getRefId()));
        if (!uploadedFile.isSuccess()) {
            throw new BadRequestException(uploadedFile.getMessage());
        }
        LampiranSk entity = LampiranSkMapper.toEntity(request, uploadedFile.getFileName(), uploadedFile.getHashedFileName(), uploadedFile.getMimeType());
        return repository.save(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        LampiranSk byId = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lampiran SK not found"));
        byId.setIsDeleted(true);
        repository.save(byId);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLampiran(EJenisSk ref, Long refId, Long id) {
        Specification<LampiranSk> specification = (root, query, cb) ->
                cb.and(
                        cb.equal(root.get("ref"), ref),
                        cb.equal(root.get("refId"), refId),
                        cb.equal(root.get("id"), id)
                );
        LampiranSk one = repository.findOne(specification)
                .orElseThrow(() -> new NotFoundException("Lampiran SK not found"));
        one.setIsDeleted(true);
        repository.save(one);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public LampiranSk acceptLampiran(LampiranSkAcceptRequest request, String oleh) {
        LampiranSk one = repository.findOne(request.getSpecification())
                .orElseThrow(() -> new NotFoundException("Lampiran SK Not Found"));
        LampiranSk entity = LampiranSkMapper.acceptEntity(one, oleh);
        return repository.save(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByRefId(Long id) {
        List<LampiranSk> list = repository.findAllByRefId(id)
                .stream().peek(lampiranSk -> lampiranSk.setIsDeleted(true))
                .toList();
        repository.saveAll(list);
    }
}
