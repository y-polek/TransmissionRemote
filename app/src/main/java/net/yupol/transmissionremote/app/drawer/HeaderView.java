package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.collect.Iterables;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class HeaderView extends RelativeLayout implements View.OnClickListener {

    private static final float CIRCLE_TEXT_PADDING_RATIO = 0.35f;

    private static final int DRAWER_ITEM_ID_ADD_SERVER = -1;
    private static final int DRAWER_ITEM_ID_MANAGE_SERVERS = -2;

    private Drawer drawer;
    private List<Server> servers;

    private TextView nameText;
    private BezelImageView serverCircleCurrent;
    private BezelImageView serverCircleSmallFirst;
    private BezelImageView serverCircleSmallSecond;

    private boolean serverListExpanded = false;
    private IconicsDrawable expandIcon;

    private Server[] serversInCircles = new Server[3];

    private HeaderListener listener;

    private ArrayList<IDrawerItem> serverSelectionItems;

    private int primaryTextColor;
    private int secondaryTextColor;

    private Drawer.OnDrawerItemClickListener drawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            if (listener == null) return false;

            int id = drawerItem.getIdentifier();
            switch (id) {
                case DRAWER_ITEM_ID_ADD_SERVER:
                    listener.onAddServerPressed();
                    return true;
                case DRAWER_ITEM_ID_MANAGE_SERVERS:
                    listener.onManageServersPressed();
                    return true;
            }

            if (id >=0 && id < servers.size()) {
                listener.onServerSelected(servers.get(id));
                drawer.closeDrawer();
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
        inflate(context, R.layout.drawer_header, this);

        TypedArray arr = context.obtainStyledAttributes(new int[] { android.R.attr.textColorPrimaryInverse, android.R.attr.textColorSecondary });
        primaryTextColor = arr.getColor(0, Color.WHITE);
        secondaryTextColor = arr.getColor(1, Color.BLACK);
        arr.recycle();

        nameText = (TextView) findViewById(R.id.name_text);

        final ImageView serverListButton = (ImageView) findViewById(R.id.server_list_button);
        expandIcon = new IconicsDrawable(context)
                .icon(serverListExpanded ? MaterialDrawerFont.Icon.mdf_arrow_drop_up : MaterialDrawerFont.Icon.mdf_arrow_drop_down)
                .color(primaryTextColor)
                .sizeRes(R.dimen.material_drawer_account_header_dropdown)
                .paddingRes(R.dimen.material_drawer_account_header_dropdown_padding);
        serverListButton.setImageDrawable(expandIcon);
        View serverTextSection = findViewById(R.id.header_text_section);
        serverTextSection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serverListExpanded) {
                    hideServersList();
                } else {
                    showServersList();
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

    public void setServers(List<Server> servers, int currentServerPosition) {
        this.servers = servers;
        if (currentServerPosition < 0) {
            Arrays.fill(serversInCircles, null);
            return;
        }

        serversInCircles[0] = servers.get(currentServerPosition);
        nameText.setText(serversInCircles[0].getName());

        Iterator<Server> nonCurrentServers = Iterables.concat(
                servers.subList(0, currentServerPosition),
                servers.subList(currentServerPosition + 1, servers.size()))
                .iterator();
        serversInCircles[1] = nonCurrentServers.hasNext() ? nonCurrentServers.next() : null;
        serversInCircles[2] = nonCurrentServers.hasNext() ? nonCurrentServers.next() : null;

        updateServerCircles();

        buildServerSelectionDrawerItems();
        if (drawer.switchedDrawerContent()) {
            showServersList(); // show updated server list
        }
    }

    private void updateServerCircles() {
        if (serversInCircles[0] != null) {
            serverCircleCurrent.setImageDrawable(new ServerDrawable(serversInCircles[0].getName(), Color.WHITE, secondaryTextColor, CIRCLE_TEXT_PADDING_RATIO));
            serverCircleCurrent.setVisibility(VISIBLE);
        } else {
            serverCircleCurrent.setVisibility(GONE);
        }

        if (serversInCircles[1] != null) {
            serverCircleSmallFirst.setImageDrawable(new ServerDrawable(serversInCircles[1].getName(), Color.WHITE, secondaryTextColor, CIRCLE_TEXT_PADDING_RATIO));
            serverCircleSmallFirst.setVisibility(VISIBLE);
        } else {
            serverCircleSmallFirst.setVisibility(GONE);
        }

        if (serversInCircles[2] != null) {
            serverCircleSmallSecond.setImageDrawable(new ServerDrawable(serversInCircles[2].getName(), Color.WHITE, secondaryTextColor, CIRCLE_TEXT_PADDING_RATIO));
            serverCircleSmallSecond.setVisibility(VISIBLE);
        } else {
            serverCircleSmallSecond.setVisibility(GONE);
        }
    }

    private void buildServerSelectionDrawerItems() {
        serverSelectionItems = new ArrayList<>(servers.size() + 2);
        for (int i=0; i<servers.size(); i++) {
            Server server = servers.get(i);
            PrimaryDrawerItem serverItem = new PrimaryDrawerItem()
                    .withName(server.getName())
                    .withIdentifier(i)
                    .withDescription(server.getHost() + ":" + server.getPort())
                    .withDescriptionTextColor(secondaryTextColor);
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
            server = serversInCircles[0];
        } else if (v == serverCircleSmallFirst) {
            server = serversInCircles[1];
        } else {
            server = serversInCircles[2];
        }

        listener.onServerSelected(server);
        drawer.closeDrawer();
    }

    public void showServersList() {
        serverListExpanded = true;
        expandIcon.icon(MaterialDrawerFont.Icon.mdf_arrow_drop_up);
        drawer.switchDrawerContent(drawerItemClickListener, null, serverSelectionItems,
                servers.indexOf(serversInCircles[0]) + drawer.getAdapter().getHeaderOffset());
    }

    private void hideServersList() {
        serverListExpanded = false;
        expandIcon.icon(MaterialDrawerFont.Icon.mdf_arrow_drop_down);
        drawer.resetDrawerContent();
    }

    public void setDrawer(Drawer drawer) {
        this.drawer = drawer;
    }

    public interface HeaderListener {
        void onSettingsPressed();
        void onServerSelected(Server server);
        void onAddServerPressed();
        void onManageServersPressed();
    }
}
