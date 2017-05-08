package digify.tv.core;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.squareup.otto.Bus;

import net.gotev.speech.Speech;
import net.gotev.speech.TextToSpeechCallback;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import digify.tv.DigifyApp;
import digify.tv.api.models.CustomerModel;
import digify.tv.injection.component.ApplicationComponent;
import digify.tv.ui.events.VideoMuteEvent;

/**
 * Created by Joel on 4/30/2017.
 */

public class CustomerProcessor {
    private Context context;
    private List<CustomerModel> models;

    @Inject
    PreferenceManager preferenceManager;

    @Inject
    Bus eventBus;


    public CustomerProcessor(Context context) {
        this.context = context;
        applicationComponent().inject(this);
        Speech.getInstance().setLocale(Locale.US);
        models = new ArrayList<>();

    }

    public void process(DataSnapshot snapshot) {
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

            CustomerModel customerModel = dataSnapshot.getValue(CustomerModel.class);
            customerModel.setAddedAt(DateTime.parse(customerModel.getTime(), ISODateTimeFormat.dateTimeParser()));

            models.add(customerModel);
        }

        if (models.isEmpty())
            return;

        CustomerModel model = models.get(0);

        models.clear();

        if (model.getServing().equals(false))
            return;

        String customerId = preferenceManager.getCustomerId();

        if (TextUtils.isEmpty(customerId) || !customerId.equals(model.getCustomerId())) {
            preferenceManager.setCustomerId(model.getCustomerId());
            speak(model.getFirstName() + " " + model.getLastName());
        }

    }

    public void speak(String name) {
        Speech.getInstance().say(name + " " + preferenceManager.getQueueMessage(), new TextToSpeechCallback() {
            @Override
            public void onStart() {

                eventBus.post(new VideoMuteEvent(VideoMuteEvent.MuteStatus.Mute));
            }

            @Override
            public void onCompleted() {
                eventBus.post(new VideoMuteEvent(VideoMuteEvent.MuteStatus.UnMute));
            }

            @Override
            public void onError() {
                Crashlytics.log("Text To Speech not working on this device.");

                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                context.startActivity(installIntent);
            }
        });
    }

    protected ApplicationComponent applicationComponent() {
        return DigifyApp.get(context).getComponent();
    }
}
