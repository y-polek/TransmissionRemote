package net.yupol.transmissionremote.app.drawer;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.materialdrawer.model.BaseDrawerItem;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.utils.TextUtils;

import java.util.List;

public class FreeSpaceFooterDrawerItem extends BaseDrawerItem<FreeSpaceFooterDrawerItem, FreeSpaceFooterDrawerItem.ViewHolder> {

    private long freeSpace = -1L;

    public FreeSpaceFooterDrawerItem withFreeSpace(long freeSpace) {
        this.freeSpace = freeSpace;
        return this;
    }

    @Override
    public int getType() {
        return R.id.drawer_item_free_space;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.drawer_footer_layout;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        boolean freeSpaceAvailable = freeSpace >= 0L;
        holder.freeSpaceText.setText(holder.freeSpaceText.getResources()
                .getString(R.string.free_space_title, freeSpaceAvailable ? TextUtils.displayableSize(freeSpace) : "…"));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView freeSpaceText;

        ViewHolder(View view) {
            super(view);
            freeSpaceText = view.findViewById(R.id.free_space_text);
        }
    }
}
