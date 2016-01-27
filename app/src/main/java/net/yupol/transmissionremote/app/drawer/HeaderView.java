package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.icons.MaterialDrawerFont;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.view.BezelImageView;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.server.Server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HeaderView extends RelativeLayout implements View.OnClickListener {

    private static final float CIRCLE_TEXT_PADDING_DP = 5f;
    private static final float SMALL_CIRCLE_TEXT_PADDING_DP = 5f;

    private static final int DRAWER_ITEM_ID_ADD_SERVER = 0;
    private static final int DRAWER_ITEM_ID_MANAGE_SERVERS = 1;

    private Drawer drawer;
    private List<Server> servers;

    private TextView nameText;
    private BezelImageView serverCircleCurrent;
    private BezelImageView serverCircleSmallFirst;
    private BezelImageView serverCircleSmallSecond;
    private final float circleTextPaddingPx;
    private final float smallCircleTextPaddingPx;

    private boolean serverListExpanded = false;
    private IconicsDrawable expandIcon;

    private Server currentServer;
    private Server[] nextServers;

    private HeaderListener listener;

    private ArrayList<IDrawerItem> serverSelectionItems;

    private int primaryTextColor;
    private int secondaryTextColor;

    private Drawer.OnDrawerItemClickListener drawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            switch (drawerItem.getIdentifier()) {
                case DRAWER_ITEM_ID_ADD_SERVER:
                    if (listener != null) listener.onAddServerPressed();
                    return true;
                case DRAWER_ITEM_ID_MANAGE_SERVERS:
                    if (listener != null) listener.onManageServersPressed();
                    return true;
            }
            return false;
        }
    };

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        circleTextPaddingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                CIRCLE_TEXT_PADDING_DP, getResources().getDisplayMetrics());
        smallCircleTextPaddingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                SMALL_CIRCLE_TEXT_PADDING_DP, getResources().getDisplayMetrics());

        inflate(context, R.layout.drawer_header, this);

        TypedArray arr = context.obtainStyledAttributes(new int[] { android.R.attr.textColorPrimaryInverse, android.R.attr.textColorSecondary });
        primaryTextColor = arr.getColor(0, Color.WHITE);
        secondaryTextColor = arr.getColor(1, Color.BLACK);
        arr.recycle();

        nameText = (TextView) findViewById(R.id.name_text);

        final ImageButton serverListButton = (ImageButton) findViewById(R.id.server_list_button);
        expandIcon = new IconicsDrawable(context)
                .icon(serverListExpanded ? MaterialDrawerFont.Icon.mdf_arrow_drop_up : MaterialDrawerFont.Icon.mdf_arrow_drop_down)
                .color(primaryTextColor)
                .sizeRes(R.dimen.material_drawer_account_header_dropdown)
                .paddingRes(R.dimen.material_drawer_account_header_dropdown_padding);
        serverListButton.setImageDrawable(expandIcon);
        serverListButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                serverListExpanded = !serverListExpanded;
                expandIcon.icon(serverListExpanded ? MaterialDrawerFont.Icon.mdf_arrow_drop_up : MaterialDrawerFont.Icon.mdf_arrow_drop_down);

                if (serverListExpanded) {
                    drawer.switchDrawerContent(drawerItemClickListener, null, serverSelectionItems,
                            servers.indexOf(currentServer) + drawer.getAdapter().getHeaderOffset());
                } else {
                    drawer.resetDrawerContent();
                }
            }
        });

        ImageButton settingsButton = (ImageButton) findViewById(R.id.settings_button);
        settingsButton.setImageDrawable(new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_settings)
                .color(primaryTextColor)
                .sizeRes(R.dimen.header_settings_size)
                .paddingRes(R.dimen.header_settings_padding));
        settingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onSettingsPressed();
            }
        });

        serverCircleCurrent = (BezelImageView) findViewById(R.id.circle_current);
        serverCircleCurrent.setOnClickListener(this);
        serverCircleSmallFirst = (BezelImageView) findViewById(R.id.circle_1);
        serverCircleSmallFirst.setOnClickListener(this);
        serverCircleSmallSecond = (BezelImageView) findViewById(R.id.circle_2);
        serverCircleSmallSecond.setOnClickListener(this);


    }

    public void setDrawer(Drawer drawer) {
        this.drawer = drawer;
    }

    public void setServers(List<Server> servers, int currentServerPosition) {
        this.servers = servers;
        if (currentServerPosition >= 0) {
            currentServer = servers.get(currentServerPosition);
            nameText.setText(currentServer.getName());
        }

        int nextServersCount = servers.size() > 0 ? Math.min(2, servers.size() - 1) : 0;
        nextServers = new Server[nextServersCount];
        int i = 0;
        Iterator<Server> it = servers.iterator();
        while (i < nextServers.length && it.hasNext()) {
            if (i != currentServerPosition) {
                nextServers[i] = it.next();
                i++;
            }
        }
        updateServerCircles();

        buildServerSelectionDrawerItems();
    }

    private void updateServerCircles() {
        if (currentServer != null) {
            serverCircleCurrent.setImageDrawable(new ServerDrawable(currentServer.getName(), Color.WHITE, secondaryTextColor, circleTextPaddingPx));
            serverCircleCurrent.setVisibility(VISIBLE);
        } else {
            serverCircleCurrent.setVisibility(GONE);
        }

        if (nextServers.length > 0) {
            serverCircleSmallFirst.setImageDrawable(new ServerDrawable(nextServers[0].getName(), Color.WHITE, secondaryTextColor, smallCircleTextPaddingPx));
            serverCircleSmallFirst.setVisibility(VISIBLE);
        } else {
            serverCircleSmallFirst.setVisibility(GONE);
        }

        if (nextServers.length > 1) {
            serverCircleSmallSecond.setImageDrawable(new ServerDrawable(nextServers[1].getName(), Color.WHITE, secondaryTextColor, smallCircleTextPaddingPx));
            serverCircleSmallSecond.setVisibility(VISIBLE);
        } else {
            serverCircleSmallSecond.setVisibility(GONE);
        }
    }

    private void buildServerSelectionDrawerItems() {
        serverSelectionItems = new ArrayList<>(servers.size() + 2);
        for (Server server : servers) {
            PrimaryDrawerItem serverItem = new PrimaryDrawerItem()
                    .withName(server.getName())
                    .withDescription(server.getHost() + ":" + server.getPort())
                    .withDescriptionTextColor(secondaryTextColor)
                    .withIcon(new ServerDrawable(server.getName(), Color.RED, Color.BLACK, smallCircleTextPaddingPx));

            serverSelectionItems.add(serverItem);
        }

        PrimaryDrawerItem addItem = new PrimaryDrawerItem()
                .withName(R.string.add_server)
                .withIcon(new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_add).paddingDp(4).color(secondaryTextColor))
                .withSelectable(false)
                .withIdentifier(DRAWER_ITEM_ID_ADD_SERVER);
        serverSelectionItems.add(addItem);

        PrimaryDrawerItem manageItem = new PrimaryDrawerItem()
                .withName(R.string.manage_servers)
                .withIcon(new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_settings).paddingDp(2).color(secondaryTextColor))
                .withSelectable(false)
                .withIdentifier(DRAWER_ITEM_ID_MANAGE_SERVERS);
        serverSelectionItems.add(manageItem);
    }

    public void setHeaderListener(HeaderListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener == null) return;

        Server server;
        if (v == serverCircleCurrent) {
            server = currentServer;
        } else if (v == serverCircleSmallFirst) {
            server = nextServers[0];
        } else {
            server = nextServers[1];
        }

        listener.onServerPressed(server);
    }

    public interface HeaderListener {
        void onSettingsPressed();
        void onServerPressed(Server server);
        void onAddServerPressed();
        void onManageServersPressed();
    }
}
