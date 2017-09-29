package nobi.tv.api.models;

/**
 * Created by Joel on 5/6/2017.
 */

public class QueueStatusModel {
    private boolean queueEnabled;

    public boolean isQueueEnabled() {
        return queueEnabled;
    }

    public void setQueueEnabled(boolean queueEnabled) {
        this.queueEnabled = queueEnabled;
    }
}
