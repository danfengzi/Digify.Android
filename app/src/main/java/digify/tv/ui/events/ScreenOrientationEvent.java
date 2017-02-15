package digify.tv.ui.events;

import digify.tv.ui.viewmodels.ScreenOrientation;

/**
 * Created by Joel on 2/14/2017.
 */

public class ScreenOrientationEvent {
    private ScreenOrientation screenOrientation;

    public ScreenOrientationEvent(ScreenOrientation screenOrientation) {
        this.screenOrientation = screenOrientation;
    }

    public ScreenOrientation getScreenOrientation() {
        return screenOrientation;
    }

    public void setScreenOrientation(ScreenOrientation screenOrientation) {
        this.screenOrientation = screenOrientation;
    }
}
