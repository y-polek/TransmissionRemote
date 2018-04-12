package net.yupol.transmissionremote.app.torrentdetails;

import net.yupol.transmissionremote.model.limitmode.IdleLimitMode;
import net.yupol.transmissionremote.model.limitmode.LimitMode;

public class IdleLimitModeAdapter extends LimitModeAdapter {
    @Override
    public int getCount() {
        return IdleLimitMode.values().length;
    }

    @Override
    public LimitMode getItem(int position) {
        return IdleLimitMode.values()[position];
    }
}
