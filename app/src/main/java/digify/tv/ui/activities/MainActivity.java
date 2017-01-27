/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package digify.tv.ui.activities;

import android.os.Bundle;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;

import javax.inject.Inject;

import digify.tv.R;
import digify.tv.core.PreferenceManager;
import digify.tv.jobs.FetchPlaylistJob;
import es.dmoral.toasty.Toasty;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends BaseActivity {

    @Inject
    JobManager jobManager;

    @Inject
    PreferenceManager preferenceManager;

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applicationComponent().inject(this);

        if (preferenceManager.isLoggedIn())
            fetchPlaylist();
    }

    public void fetchPlaylist() {
        Toasty.info(this, "Checking for updates...", Toast.LENGTH_SHORT, true).show();

        jobManager.addJobInBackground(new FetchPlaylistJob());

    }

}
