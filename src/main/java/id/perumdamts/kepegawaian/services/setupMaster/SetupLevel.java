package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.repositories.master.LevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SetupLevel implements SetupMaster {
    private final LevelRepository levelRepository;
    private final TransactionTemplate transactionTemplate;

    @Transactional
    @Override
    public void insertBatch() {
        transactionTemplate.execute(status -> {
            try {
                List<Level> levels = createLevelData();
                for (Level level : levels) {
                    levelRepository.saveAndFlush(level);// Flush setelah setiap save
                }

                return null;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("error: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to setup levels", e);
            }
        });
    }

    private List<Level> createLevelData() {
        return List.of(
                new Level("DEWAS"),
                new Level("DIRUT"),
                new Level("DIRTEK"),
                new Level("DIRUM"),
                new Level("MANAJER"),
                new Level("SUPERVISOR"),
                new Level("STAF")
        );
    }
}
