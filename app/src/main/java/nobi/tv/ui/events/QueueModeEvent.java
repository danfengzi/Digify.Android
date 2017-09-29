package nobi.tv.ui.events;

/**
 * Created by Joel on 5/3/2017.
 */

public class QueueModeEvent {
    private boolean isEnabled;

    public QueueModeEvent(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
