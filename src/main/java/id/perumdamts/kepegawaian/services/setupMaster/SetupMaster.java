package id.perumdamts.kepegawaian.services.setupMaster;

public interface SetupMaster {

    void insertBatch();

    default int getOrder() {
        return 0;
    }
}
