package id.perumdamts.kepegawaian.services.cuti.approval;

import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalRequest;
import id.perumdamts.kepegawaian.repositories.cuti.CutiApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CutiApprovalQueryService {
    private final CutiApprovalRepository repository;

    public Page<CutiApprovalMiniResponse> findPage(Long cutiId, CutiApprovalRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(CutiApprovalMiniResponse::from);
    }
}
