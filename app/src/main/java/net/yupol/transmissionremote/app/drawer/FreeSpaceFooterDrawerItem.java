package net.yupol.transmissionremote.app.drawer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.materialdrawer.model.BaseDrawerItem;
import com.mikepenz.materialdrawer.model.utils.ViewHolderFactory;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.utils.TextUtils;

public class FreeSpaceFooterDrawerItem extends BaseDrawerItem {

    private long freeSpace = -1L;

    public FreeSpaceFooterDrawerItem withFreeSpace(long freeSpace) {
        this.freeSpace = freeSpace;
        return this;
    }

    @Override
    public String getType() {
        return "FOOTER_ITEM";
    }

    @Override
    public int getLayoutRes() {
        return R.layout.drawer_footer_layout;
    }

    @Override
    public void bindView(RecyclerView.ViewHolder holder) {
        ViewHolder viewHolder = (ViewHolder) holder;

        boolean freeSpaceAvailable = freeSpace >= 0L;
        viewHolder.freeSpaceText.setText(viewHolder.freeSpaceText.getResources()
                .getString(R.string.free_space_title, freeSpaceAvailable ? TextUtils.displayableSize(freeSpace) : "…"));
    }

    @Override
    public ViewHolderFactory getFactory() {
        return new ItemFactory();
    }

    public static class ItemFactory implements ViewHolderFactory<FreeSpaceFooterDrawerItem.ViewHolder> {
        @Override
        public FreeSpaceFooterDrawerItem.ViewHolder factory(View v) {
            return new FreeSpaceFooterDrawerItem.ViewHolder(v);
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView freeSpaceText;

        ViewHolder(View view) {
            super(view);
            freeSpaceText = view.findViewById(R.id.free_space_text);
        }
    }
}
