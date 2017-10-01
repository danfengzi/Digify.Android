package genius.tv.ui.events;

/**
 * Created by Joel on 5/1/2017.
 */

public class VideoMuteEvent {

    public enum MuteStatus
    {
        Mute,
        UnMute
    }

    public MuteStatus muteStatus;

    public VideoMuteEvent(MuteStatus muteStatus) {
        this.muteStatus = muteStatus;
    }

    public MuteStatus getMuteStatus() {
        return muteStatus;
    }

}
