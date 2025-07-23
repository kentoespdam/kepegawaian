package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainRequest;
import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainResponse;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CutiApprovalChainCustomRepositoryImpl implements CutiApprovalChainCustomRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<CutiApprovalChainResponse> findPageApproval(CutiApprovalChainRequest request) {
        List<CutiApprovalChainResponse> content = findListMaxReadWriteStatus(request).stream()
                .map(CutiApprovalChainResponse::from).toList();
        Long total = count(request);
        return new PageImpl<>(content, request.getPageable(), total);
    }

    private List<CutiApprovalChain> findListMaxReadWriteStatus(CutiApprovalChainRequest request) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CutiApprovalChain> query = cb.createQuery(CutiApprovalChain.class);
        Root<CutiApprovalChain> root = query.from(CutiApprovalChain.class);
        Join<CutiApprovalChain, CutiPegawai> cutiPegawaiJoin = root.join("refCuti", JoinType.INNER);

        query.select(cb.construct(
                CutiApprovalChain.class,
                root.get("id"),
                cutiPegawaiJoin,
                root.get("jabatanId"),
                root.get("jabatanNama"),
                root.get("approvalLevel"),
                root.get("approvalStatus"),
                cb.max(root.get("readWriteStatus")).alias("readWriteStatus"))
        );

        Predicate predicate = request.getApprovalChainSpecification().toPredicate(root, query, cb);
        query.where(predicate);
        query.groupBy(root.get("refCuti").get("id"));

        return em.createQuery(query)
                .setFirstResult(request.getPageable().getPageNumber() * request.getPageable().getPageSize())
                .setMaxResults(request.getPageable().getPageSize())
                .getResultList();
    }

    private Long count(CutiApprovalChainRequest request) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CutiApprovalChain> root = query.from(CutiApprovalChain.class);
        query.select(cb.countDistinct(root.get("refCuti").get("id")));
        query.where(request.getApprovalChainSpecification().toPredicate(root, query, cb));
        return em.createQuery(query).getSingleResult();
    }
}
