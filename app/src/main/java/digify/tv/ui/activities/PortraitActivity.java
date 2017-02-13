package digify.tv.ui.activities;

import android.app.Activity;
import android.os.Bundle;

import com.daimajia.slider.library.SliderLayout;

import digify.tv.R;

public class PortraitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portrait);
    }

    public void generateSlider()
    {
        SliderLayout sliderLayout = (SliderLayout) getLayoutInflater().inflate(R.layout.portrait_image_slider_layout,null);

    }

}
