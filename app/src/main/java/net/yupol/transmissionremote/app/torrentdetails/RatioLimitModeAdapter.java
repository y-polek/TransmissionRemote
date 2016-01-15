package net.yupol.transmissionremote.app.torrentdetails;

import net.yupol.transmissionremote.app.model.limitmode.LimitMode;
import net.yupol.transmissionremote.app.model.limitmode.RatioLimitMode;

public class RatioLimitModeAdapter extends LimitModeAdapter {
    @Override
    public int getCount() {
        return RatioLimitMode.values().length;
    }

    @Override
    public LimitMode getItem(int position) {
        return RatioLimitMode.values()[position];
    }
}
