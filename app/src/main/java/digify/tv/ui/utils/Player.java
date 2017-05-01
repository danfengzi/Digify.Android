package digify.tv.ui.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by Joel on 5/1/2017.
 */

public class Player extends VideoView implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;

    public Player(Context context, AttributeSet attributes) {
        super(context, attributes);

        this.setOnPreparedListener(this);
        this.setOnCompletionListener(this);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {  }

    public void mute() {
        this.setVolume(0);
    }

    public void unmute() {
        this.setVolume(100);
    }

    private void setVolume(int amount) {
        final int max = 100;
        final double numerator = max - amount > 0 ? Math.log(max - amount) : 0;
        final float volume = (float) (1 - (numerator / Math.log(max)));

        this.mediaPlayer.setVolume(volume, volume);
    }
}