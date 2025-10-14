package id.perumdamts.kepegawaian.config.audit;

public class AuditRevisionListener {
}
//        implements RevisionListener {
//    @Override
//    public void newRevision(Object revisionEntity) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        AppwriteUser principal = (AppwriteUser) authentication.getPrincipal();
//        AuditRevisionEntity audit = (AuditRevisionEntity) revisionEntity;
//        audit.setUsername(principal.getName());
//    }
//}
