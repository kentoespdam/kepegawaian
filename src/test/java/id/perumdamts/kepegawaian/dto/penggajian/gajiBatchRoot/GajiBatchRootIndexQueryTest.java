package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GajiBatchRootIndexQueryTest {

    @Test
    void defaultSortDirection_isDesc() {
        GajiBatchRootIndexQuery query = new GajiBatchRootIndexQuery();
        assertEquals("desc", query.getSortDirection());
    }

    @Test
    void explicitAscWithSortColumn_isPreserved() {
        GajiBatchRootIndexQuery query = new GajiBatchRootIndexQuery();
        query.setSortBy("periode");
        query.setSortDirection("asc");
        assertEquals("asc", query.getSortDirection());
    }
}