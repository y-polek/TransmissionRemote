package net.yupol.transmissionremote.app.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.domain.model.Server;
import net.yupol.transmissionremote.domain.repository.ServerRepository;

import javax.inject.Inject;

public class AddServerActivity extends AppCompatActivity implements ServerDetailsFragment.OnServerActionListener {

    public static final String PARAM_CANCELABLE = "param_cancelable";
    public static final String EXTRA_SEVER_NAME = "extra_server_name";

    private ServerDetailsFragment serverDetailsFragment;

    @Inject ServerRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TransmissionRemote.getInstance().appComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_server_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }

        FragmentManager fm = getSupportFragmentManager();
        serverDetailsFragment = (ServerDetailsFragment) fm.findFragmentById(R.id.add_server_fragment_container);
        if (serverDetailsFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            serverDetailsFragment = new ServerDetailsFragment();
            ft.add(R.id.add_server_fragment_container, serverDetailsFragment);
            ft.commit();
        }

        Button okButton = findViewById(R.id.ok_button);
        okButton.setOnClickListener(view -> {
            Server server = serverDetailsFragment.getNewServer();
            if (server != null) {
                repo.addServer(server);
                setResult(RESULT_OK, new Intent().putExtra(EXTRA_SEVER_NAME, server.name));
                finish();
            }
        });

        Button cancelButton = findViewById(R.id.cancel_button);
        boolean isCancelable = getIntent().getBooleanExtra(PARAM_CANCELABLE, true);
        cancelButton.setVisibility(isCancelable ? View.VISIBLE : View.GONE);
        if (isCancelable) {
            cancelButton.setOnClickListener(view -> cancel());
        }
    }

    @Override
    public void onSaveServerRequested() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancel();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    public static Intent intent(Context context) {
        return new Intent(context, AddServerActivity.class);
    }
}
