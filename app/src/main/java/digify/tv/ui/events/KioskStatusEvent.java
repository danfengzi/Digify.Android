package digify.tv.ui.events;

/**
 * Created by Joel on 5/8/2017.
 */

public class KioskStatusEvent {
    private boolean enabled;

    public KioskStatusEvent(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
