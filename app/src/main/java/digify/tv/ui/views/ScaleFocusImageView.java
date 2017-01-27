package digify.tv.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Joel on 1/27/2017.
 */

public class ScaleFocusImageView extends ImageView {
    private Context mContext;
    boolean flag;

    public ScaleFocusImageView(Context context, AttributeSet attr) {
        super(context, attr);
        mContext = context;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            zoom(1f, 1f, new PointF(getWidth() / 2, getHeight() / 2));
        } else {
            zoom(2f, 2f, new PointF(getWidth() / 2, getHeight() / 2));
        }

    }

    /** zooming is done from here */
    @SuppressLint("NewApi")
    public void zoom(Float scaleX, Float scaleY, PointF pivot) {
        setPivotX(pivot.x);
        setPivotY(pivot.y);
        setScaleX(scaleX);
        setScaleY(scaleY);
    }
}
