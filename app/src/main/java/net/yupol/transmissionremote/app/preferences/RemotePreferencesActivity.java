package net.yupol.transmissionremote.app.preferences;

import android.app.Activity;
import android.os.Bundle;

public class RemotePreferencesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new RemotePreferencesFragment()).commit();
    }
}
