package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import org.springframework.context.ApplicationEvent;

public class GajiBatchRootProcessEvent extends ApplicationEvent {
    private final String rootBatchId;

    public GajiBatchRootProcessEvent(Object source, String rootBatchId) {
        super(source);
        this.rootBatchId = rootBatchId;
    }

    public String getRootBatchId() {
        return rootBatchId;
    }
}
