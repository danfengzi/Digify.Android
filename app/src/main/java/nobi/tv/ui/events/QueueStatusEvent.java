package nobi.tv.ui.events;

/**
 * Created by Joel on 5/8/2017.
 */

public class QueueStatusEvent {
    private boolean enabled;

    public QueueStatusEvent(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
