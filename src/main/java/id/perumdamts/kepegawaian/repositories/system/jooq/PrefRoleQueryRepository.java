package id.perumdamts.kepegawaian.repositories.system.jooq;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.system.roles.PrefRoleRequest;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.PrefRole.PREF_ROLE;

@Repository
@RequiredArgsConstructor
public class PrefRoleQueryRepository {
    private final DSLContext dsl;

    public Page<PrefRole> findPage(PrefRoleRequest request) {
        var sortOrder = SortParam.resolve(request.getSortBy(), request.getSortDirection(), allowedSorts(), PREF_ROLE.ID);
        Condition where = baseWhere(request);

        var count = dsl.selectCount()
                .from(PREF_ROLE)
                .where(where)
                .fetchOptional(0, Long.class)
                .orElse(0L);

        var data = dsl.select(
                        PREF_ROLE.ID,
                        PREF_ROLE.DESCRIPTION
                )
                .from(PREF_ROLE)
                .where(where)
                .orderBy(sortOrder)
                .limit(request.getSizeOrDefault())
                .offset(request.getPageNumber() * request.getSizeOrDefault())
                .fetch(record -> new PrefRole(record.get(PREF_ROLE.ID), record.get(PREF_ROLE.DESCRIPTION)));

        return new PageImpl<>(data, PageRequest.of(request.getPageNumber(), request.getSizeOrDefault()), count);
    }

    public List<PrefRole> listQuery() {
        return dsl.select(PREF_ROLE.ID, PREF_ROLE.DESCRIPTION)
                .from(PREF_ROLE)
                .orderBy(PREF_ROLE.ID.asc())
                .fetch(record -> new PrefRole(record.get(PREF_ROLE.ID), record.get(PREF_ROLE.DESCRIPTION)));
    }

    public Optional<PrefRole> getById(String id) {
        return dsl.select(PREF_ROLE.ID, PREF_ROLE.DESCRIPTION)
                .from(PREF_ROLE)
                .where(PREF_ROLE.ID.eq(id))
                .fetchOptional(record -> new PrefRole(record.get(PREF_ROLE.ID), record.get(PREF_ROLE.DESCRIPTION)));
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "id", PREF_ROLE.ID,
                "description", PREF_ROLE.DESCRIPTION
        );
    }

    private Condition baseWhere(PrefRoleRequest q) {
        return DSL.trueCondition()
                .and(q.getId() != null ? PREF_ROLE.ID.likeIgnoreCase("%" + q.getId() + "%") : DSL.noCondition());
    }
}
