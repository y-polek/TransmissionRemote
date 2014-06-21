package net.yupol.transmissionremote.app.server;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import net.yupol.transmissionremote.app.R;

public class AddServerActivity extends Activity {

    public static final String PARAM_CANCELABLE = "param_cancelable";
    public static final String EXTRA_SEVER = "extra_server";

    private AddServerFragment addServerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_server_activity);
        setTitle(getString(R.string.add_new_server_title));

        FragmentManager fm = getFragmentManager();
        addServerFragment = (AddServerFragment) fm.findFragmentById(R.id.add_server_fragment_container);
        if (addServerFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            addServerFragment = new AddServerFragment();
            ft.add(R.id.add_server_fragment_container, addServerFragment);
            ft.commit();
        }

        Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Server server = addServerFragment.getServer();
                if (server != null) {
                    setResult(RESULT_OK, new Intent().putExtra(EXTRA_SEVER, server));
                    finish();
                }
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        boolean isCancelable = getIntent().getBooleanExtra(PARAM_CANCELABLE, true);
        cancelButton.setVisibility(isCancelable ? View.VISIBLE : View.GONE);
        if (isCancelable) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            });
        }
    }
}
