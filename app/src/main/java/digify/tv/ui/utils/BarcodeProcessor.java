package digify.tv.ui.utils;

import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joel on 2/16/2017.
 */

public class BarcodeProcessor {

    public static List<Integer> getNumberBasedKeyCodes() {
        List<Integer> keys = new ArrayList<>();
        keys.add(KeyEvent.KEYCODE_0);
        keys.add(KeyEvent.KEYCODE_1);
        keys.add(KeyEvent.KEYCODE_2);
        keys.add(KeyEvent.KEYCODE_3);
        keys.add(KeyEvent.KEYCODE_4);
        keys.add(KeyEvent.KEYCODE_5);
        keys.add(KeyEvent.KEYCODE_6);
        keys.add(KeyEvent.KEYCODE_7);
        keys.add(KeyEvent.KEYCODE_8);
        keys.add(KeyEvent.KEYCODE_9);


        return keys;
    }
}
