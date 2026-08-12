package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;

/**
 * Per-entity leaf operations for the generic approval engine
 * {@link ProfileUpdateApprovalHandler} (ADR-0036 §5, §7).
 * The engine owns the flow; strategies own explicit per-entity setters
 * (load-and-set-and-save — NOT blind reflection, NOT new bulk JPQL).
 */
public interface ProfileUpdateStrategy {
    EProfileUpdateTable table();

    /** APPROVED: mark the row stable (changedStatus=false, + stamp for stamp entities). */
    void markAsStable(String revId);

    /** DELETE rejected: reactivate soft-deleted row (isDeleted=false, changedStatus=false). */
    void resetEntityState(String revId);

    /** INSERT rejected: row never legitimately existed → delete it (lampiran: + delete physical file). */
    void handleRejectedInsert(String revId);

    /** UPDATE rejected: restore fields from previous Envers revision. */
    void revertToPreviousRevision(ProfileUpdate profileUpdate);
}
