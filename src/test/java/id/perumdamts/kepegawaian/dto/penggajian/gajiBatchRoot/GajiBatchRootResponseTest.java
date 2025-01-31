package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class GajiBatchRootResponseTest {
    @Autowired
    private GajiBatchRootRepository repository;

    @Test
    @Transactional
    void response() {
        List<GajiBatchRoot> all = repository.findAll();
        List<GajiBatchRootResponse> list = all.stream().map(GajiBatchRootResponse::from).toList();
        assertNotNull(list);
    }
}