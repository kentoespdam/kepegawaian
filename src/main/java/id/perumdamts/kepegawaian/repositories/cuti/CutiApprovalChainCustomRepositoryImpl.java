package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainResponse;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiApprovalChainRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CutiApprovalChainCustomRepositoryImpl implements CutiApprovalChainCustomRepository {
    @PersistenceContext
    private EntityManager em;

    public Page<CutiApprovalChainResponse> findPage(CutiApprovalChainRequest request) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CutiApprovalChain> query = cb.createQuery(CutiApprovalChain.class);
        Root<CutiApprovalChain> root = query.from(CutiApprovalChain.class);

        Predicate predicate = request.getApprovalChainSpecification().toPredicate(root, query, cb);
        query.groupBy(root.get("refCuti").get("id"));
        query.where(predicate);

        List<CutiApprovalChainResponse> values = em.createQuery(query)
                .setFirstResult(request.getPageable().getPageNumber() * request.getPageable().getPageSize())
                .setMaxResults(request.getPageable().getPageSize())
                .getResultStream()
                .map(CutiApprovalChainResponse::from)
                .toList();

        Long total = count(request);
        return new PageImpl<>(values, request.getPageable(), total);
    }

    public Long count(CutiApprovalChainRequest request) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CutiApprovalChain> root = query.from(CutiApprovalChain.class);
        query.select(cb.countDistinct(root.get("refCuti").get("id")));
        query.where(request.getApprovalChainSpecification().toPredicate(root, query, cb));
        return em.createQuery(query).getSingleResult();
    }
}
