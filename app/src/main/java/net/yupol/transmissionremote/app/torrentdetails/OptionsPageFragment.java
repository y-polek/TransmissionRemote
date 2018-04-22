package net.yupol.transmissionremote.app.torrentdetails;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.databinding.TorrentDetailsOptionsPageFragmentBinding;
import net.yupol.transmissionremote.app.utils.MinMaxTextWatcher;
import net.yupol.transmissionremote.model.Parameter;
import net.yupol.transmissionremote.model.json.ServerSettings;
import net.yupol.transmissionremote.model.json.TorrentInfo;
import net.yupol.transmissionremote.model.json.TransferPriority;
import net.yupol.transmissionremote.model.limitmode.IdleLimitMode;
import net.yupol.transmissionremote.model.limitmode.LimitMode;
import net.yupol.transmissionremote.model.limitmode.RatioLimitMode;
import net.yupol.transmissionremote.transport.Transport;
import net.yupol.transmissionremote.transport.rpc.RpcArgs;
import net.yupol.transmissionremote.transport.rpc.TorrentParameters;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static net.yupol.transmissionremote.transport.rpc.TorrentParameters.transferPriority;

public class OptionsPageFragment extends BasePageFragment implements
        BandwidthLimitFragment.OnBandwidthLimitChangedListener {

    private static final String TAG = OptionsPageFragment.class.getSimpleName();

    private Transport transport;

    private ServerSettings serverSettings;

    private BandwidthLimitFragment bandwidthLimitFragment;

    private boolean viewCreated;
    private TorrentDetailsOptionsPageFragmentBinding binding;
    private CompositeDisposable requests = new CompositeDisposable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        transport = new Transport(TransmissionRemote.getInstance().getActiveServer());

        transport.api().serverSettings(ImmutableMap.<String, Object>of())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ServerSettings>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        requests.add(d);
                    }

                    @Override
                    public void onSuccess(ServerSettings settings) {
                        serverSettings = settings;
                        updateSeedingLimitsUi(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Failed to retrieve server settings", e);
                        // TODO: retry
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.torrent_details_options_page_fragment, container, false);
        bandwidthLimitFragment = (BandwidthLimitFragment)
                getChildFragmentManager().findFragmentById(R.id.bandwidth_limit_fragment);

        final TransferPrioritySpinnerAdapter transferPriorityAdapter = new TransferPrioritySpinnerAdapter();
        binding.prioritySpinner.setAdapter(transferPriorityAdapter);
        binding.prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TransferPriority priority = transferPriorityAdapter.getItem(position);
                sendSaveOptionRequest(transferPriority(priority));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        binding.stayWithGlobalBandwidthCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                sendSaveOptionRequest(TorrentParameters.honorSessionLimits(isChecked));
            }
        });

        binding.ratioLimitSpinner.setAdapter(new RatioLimitModeAdapter());
        binding.ratioLimitEdit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10)} );
        binding.ratioLimitEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveRatioLimit();
                }
                return false;
            }
        });
        binding.ratioLimitEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    saveRatioLimit();
                }
            }
        });

        binding.idleLimitSpinner.setAdapter(new IdleLimitModeAdapter());
        binding.idleLimitEdit.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(5)} );
        binding.idleLimitEdit.addTextChangedListener(new MinMaxTextWatcher(1, 0xFFFF));
        binding.idleLimitEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveIdleLimit();
                }
                return false;
            }
        });
        binding.idleLimitEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    saveIdleLimit();
                }
            }
        });

        if (getTorrentInfo() == null) {
            binding.contentView.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            showContent();
            updateUi(true);
        }

        // Postpone listeners registration to avoid notifying listeners on layout.
        // http://stackoverflow.com/questions/2562248/how-to-keep-onitemselected-from-firing-off-on-a-newly-instantiated-spinner
        binding.getRoot().post(new Runnable() {
            @Override
            public void run() {
                binding.ratioLimitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        updateSeedingLimitsUi(false);
                        LimitMode mode = (LimitMode) parent.getAdapter().getItem(position);
                        sendSaveOptionRequest(TorrentParameters.seedRatioMode(mode));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                });

                binding.idleLimitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        updateSeedingLimitsUi(false);
                        LimitMode mode = (LimitMode) parent.getAdapter().getItem(position);
                        sendSaveOptionRequest(TorrentParameters.seedIdleMode(mode));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                });
            }
        });

        viewCreated = true;

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewCreated = false;
    }

    @Override
    public void onStop() {
        requests.clear();
        super.onStop();
    }

    @Override
    public void setTorrentInfo(TorrentInfo torrentInfo) {
        boolean isUpdate = getTorrentInfo() != null;
        super.setTorrentInfo(torrentInfo);
        if (viewCreated) {
            showContent();
            if (!isUpdate) {
                updateUi(true);
            } else {
                // TODO: implement UI updates
            }
        }
    }

    private void showContent() {
        binding.contentView.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void updateUi(boolean syncWithModel) {
        TorrentInfo torrentInfo = getTorrentInfo();

        binding.prioritySpinner.setSelection(torrentInfo.getTransferPriority().ordinal());

        binding.stayWithGlobalBandwidthCheckbox.setChecked(torrentInfo.isSessionLimitsHonored());

        bandwidthLimitFragment.setDownloadLimited(torrentInfo.isDownloadLimited());
        bandwidthLimitFragment.setDownloadLimit(torrentInfo.getDownloadLimit());
        bandwidthLimitFragment.setUploadLimited(torrentInfo.isUploadLimited());
        bandwidthLimitFragment.setUploadLimit(torrentInfo.getUploadLimit());

        binding.ratioLimitSpinner.setSelection(torrentInfo.getSeedRatioMode().ordinal());
        binding.idleLimitSpinner.setSelection(torrentInfo.getSeedIdleMode().ordinal());
        updateSeedingLimitsUi(syncWithModel);
    }

    private void updateSeedingLimitsUi(boolean syncWithModel) {
        TorrentInfo torrentInfo = getTorrentInfo();

        switch (getRatioLimitMode()) {
            case STOP_AT_RATIO:
                if (syncWithModel || binding.ratioLimitEdit.getText().length() == 0) {
                    binding.ratioLimitEdit.setText(String.valueOf(torrentInfo.getSeedRatioLimit()));
                }
                binding.ratioLimitEdit.setVisibility(View.VISIBLE);
                binding.ratioLimitEdit.setEnabled(true);
                binding.ratioLimitGlobalText.setVisibility(View.INVISIBLE);
                break;
            case GLOBAL_SETTINGS:
                if (serverSettings != null) {
                    String text = serverSettings.isSeedRatioLimited()
                            ? String.valueOf(serverSettings.getSeedRatioLimit())
                            : getString(R.string.disabled);
                    binding.ratioLimitGlobalText.setText(text);
                } else {
                    binding.ratioLimitGlobalText.setText(R.string.three_dots);
                }
                binding.ratioLimitEdit.setVisibility(View.INVISIBLE);
                binding.ratioLimitGlobalText.setVisibility(View.VISIBLE);
                break;
            case UNLIMITED:
                if (syncWithModel || binding.ratioLimitEdit.getText().length() == 0) {
                    binding.ratioLimitEdit.setText(String.valueOf(torrentInfo.getSeedRatioLimit()));
                }
                binding.ratioLimitEdit.setVisibility(View.VISIBLE);
                binding.ratioLimitEdit.setEnabled(false);
                binding.ratioLimitGlobalText.setVisibility(View.INVISIBLE);
                break;
        }

        switch (getIdleLimitMode()) {
            case STOP_WHEN_INACTIVE:
                if (syncWithModel || binding.idleLimitEdit.getText().length() == 0) {
                    binding.idleLimitEdit.setText(String.valueOf(torrentInfo.getSeedIdleLimit()));
                }
                binding.idleLimitEdit.setVisibility(View.VISIBLE);
                binding.idleLimitEdit.setEnabled(true);
                binding.idleLimitGlobalText.setVisibility(View.INVISIBLE);
                break;
            case GLOBAL_SETTINGS:
                if (serverSettings != null) {
                    String text = serverSettings.isSeedIdleLimited()
                            ? String.valueOf(serverSettings.getSeedIdleLimit())
                            : getString(R.string.disabled);
                    binding.idleLimitGlobalText.setText(text);
                }
                binding.idleLimitEdit.setVisibility(View.INVISIBLE);
                binding.idleLimitGlobalText.setVisibility(View.VISIBLE);
                break;
            case UNLIMITED:
                if (syncWithModel || binding.idleLimitEdit.getText().length() == 0) {
                    binding.idleLimitEdit.setText(String.valueOf(torrentInfo.getSeedIdleLimit()));
                }
                binding.idleLimitEdit.setVisibility(View.VISIBLE);
                binding.idleLimitEdit.setEnabled(false);
                binding.idleLimitGlobalText.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private RatioLimitMode getRatioLimitMode() {
        return (RatioLimitMode) binding.ratioLimitSpinner.getSelectedItem();
    }

    private double getRatioLimit() {
        try {
            return Double.parseDouble(binding.ratioLimitEdit.getText().toString());
        } catch (NumberFormatException e) {
            return getTorrentInfo().getSeedRatioLimit();
        }
    }

    private IdleLimitMode getIdleLimitMode() {
        return (IdleLimitMode) binding.idleLimitSpinner.getSelectedItem();
    }

    private int getIdleLimit() {
        try {
            return Integer.parseInt(binding.idleLimitEdit.getText().toString());
        } catch (NumberFormatException e) {
            return getTorrentInfo().getSeedIdleLimit();
        }
    }

    @Override
    public void onDownLimitEnabledChanged(boolean isEnabled) {
        sendSaveOptionRequest(TorrentParameters.downloadLimited(isEnabled));
    }

    @Override
    public void onDownLimitChanged(int limit) {
        sendSaveOptionRequest(TorrentParameters.downloadLimit(limit));
    }

    @Override
    public void onUpLimitEnabledChanged(boolean isEnabled) {
        sendSaveOptionRequest(TorrentParameters.uploadLimited(isEnabled));
    }

    @Override
    public void onUpLimitChanged(int limit) {
        sendSaveOptionRequest(TorrentParameters.uploadLimit(limit));
    }

    private void saveRatioLimit() {
        double limit = getRatioLimit();
        sendSaveOptionRequest(TorrentParameters.seedRatioLimit(limit));
    }

    private void saveIdleLimit() {
        int limit = getIdleLimit();
        sendSaveOptionRequest(TorrentParameters.seedIdleLimit(limit));
    }

    private void sendSaveOptionRequest(Parameter<String, ?> option) {
        int torrentId = getTorrent().getId();
        transport.api().setTorrentSettings(RpcArgs.parameters(torrentId, option))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorComplete()
                .subscribe();
    }

    private void sendTorrentUpdateRequest() {
        transport.api().torrentInfo(getTorrent().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<TorrentInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        requests.add(d);
                    }

                    @Override
                    public void onSuccess(TorrentInfo torrentInfo) {
                        setTorrentInfo(torrentInfo);
                        Toast.makeText(getActivity(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), getString(R.string.options_update_failed), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
