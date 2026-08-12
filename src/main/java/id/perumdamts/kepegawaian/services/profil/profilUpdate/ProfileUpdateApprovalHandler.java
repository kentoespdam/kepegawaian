package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generic approval engine (ADR-0036 §5, §7): one class manages the flow
 * (changeHandler/markAsStable/resetEntityState/handleRejected), delegating
 * per-entity setters to {@link ProfileUpdateStrategy} implementations.
 * Revert = load-and-set-and-save; the engine never touches entities directly.
 */
@Service
public class ProfileUpdateApprovalHandler {
    private final Map<EProfileUpdateTable, ProfileUpdateStrategy> strategies;

    public ProfileUpdateApprovalHandler(List<ProfileUpdateStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(ProfileUpdateStrategy::table, Function.identity()));
    }

    /**
     * Generic flow: APPROVED → markAsStable; REJECT → switch on action type
     * (INSERT → delete row, UPDATE → revert revision, DELETE → reactivate).
     */
    public void changeHandler(ProfileUpdate profileUpdate, EProfileUpdateApproval approval) {
        ProfileUpdateStrategy strategy = strategies.get(profileUpdate.getTableName());
        if (strategy == null) {
            throw new IllegalStateException("No approval strategy for table: " + profileUpdate.getTableName());
        }
        if (approval == EProfileUpdateApproval.APPROVED) {
            strategy.markAsStable(profileUpdate.getRevId());
            return;
        }
        switch (profileUpdate.getActionType()) {
            case INSERT -> strategy.handleRejectedInsert(profileUpdate.getRevId());
            case UPDATE -> strategy.revertToPreviousRevision(profileUpdate);
            case DELETE -> strategy.resetEntityState(profileUpdate.getRevId());
            default -> throw new IllegalStateException("Unexpected action type: " + profileUpdate.getActionType());
        }
    }
}
