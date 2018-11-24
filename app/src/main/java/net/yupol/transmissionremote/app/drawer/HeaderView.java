package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.utils.ColorUtils;
import net.yupol.transmissionremote.domain.model.Server;
import net.yupol.transmissionremote.domain.repository.ServerRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

public class HeaderView extends RelativeLayout implements View.OnClickListener {

    private static final String TAG = HeaderView.class.getSimpleName();

    private static final float CIRCLE_TEXT_PADDING_RATIO = 0.35f;

    private static final int DRAWER_ITEM_ID_ADD_SERVER = -101;
    private static final int DRAWER_ITEM_ID_MANAGE_SERVERS = -102;

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

    private int primaryInverseTextColor;
    private int secondaryTextColor;

    @Inject ServerRepository repo;

    private Drawer.OnDrawerItemClickListener drawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            if (listener == null) return false;

            int id = (int) drawerItem.getIdentifier();
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

        TransmissionRemote.getInstance().appComponent().inject(this);

        inflate(context, R.layout.drawer_header, this);

        primaryInverseTextColor = ColorUtils.resolveColor(context, android.R.attr.textColorPrimaryInverse, R.color.text_primary_inverse);
        secondaryTextColor = ColorUtils.resolveColor(context, android.R.attr.textColorSecondary, R.color.text_secondary);

        nameText = findViewById(R.id.name_text);

        final ImageView serverListButton = findViewById(R.id.server_list_button);
        expandIcon = new IconicsDrawable(context)
                .icon(serverListExpanded ? MaterialDrawerFont.Icon.mdf_arrow_drop_up : MaterialDrawerFont.Icon.mdf_arrow_drop_down)
                .color(primaryInverseTextColor)
                .sizeRes(R.dimen.material_drawer_account_header_dropdown)
                .paddingRes(R.dimen.material_drawer_account_header_dropdown_padding);
        serverListButton.setImageDrawable(expandIcon);
        View serverTextSection = findViewById(R.id.header_text_section);
        serverTextSection.setOnClickListener(v -> {
            if (serverListExpanded) {
                hideServersList();
            } else {
                showServersList();
            }
        });

        ImageButton settingsButton = findViewById(R.id.settings_button);
        settingsButton.setImageDrawable(new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_settings)
                .color(primaryInverseTextColor)
                .sizeRes(R.dimen.header_settings_size)
                .paddingRes(R.dimen.header_settings_padding));
        settingsButton.setOnClickListener(v -> {
            if (listener != null) listener.onSettingsPressed();
        });

        serverCircleCurrent = findViewById(R.id.circle_current);
        serverCircleCurrent.setOnClickListener(this);
        serverCircleSmallFirst = findViewById(R.id.circle_1);
        serverCircleSmallFirst.setOnClickListener(this);
        serverCircleSmallSecond = findViewById(R.id.circle_2);
        serverCircleSmallSecond.setOnClickListener(this);
    }

    public void setServers(List<Server> servers, Server activeServer) {
        this.servers = servers;

        Arrays.fill(serversInCircles, null);
        if (servers.isEmpty()) return;
        int activeServerPosition = Math.max(servers.indexOf(activeServer), 0);

        List<Server> orderedServers = new ArrayList<>(servers);
        Collections.swap(orderedServers, 0, activeServerPosition);

        int i = 0;
        Iterator<Server> it = orderedServers.iterator();
        while (it.hasNext() && i < serversInCircles.length) {
            serversInCircles[i++] = it.next();
        }

        nameText.setText(serversInCircles[0].name);

        updateServerCircles();

        buildServerSelectionDrawerItems();
        if (drawer.switchedDrawerContent()) {
            showServersList(); // show updated server list
        }
    }

    private void updateServerCircles() {
        if (serversInCircles[0] != null) {
            serverCircleCurrent.setImageDrawable(serverDrawable(serversInCircles[0].name));
            serverCircleCurrent.setVisibility(VISIBLE);
        } else {
            serverCircleCurrent.setVisibility(GONE);
        }

        if (serversInCircles[1] != null) {
            serverCircleSmallFirst.setImageDrawable(serverDrawable(serversInCircles[1].name));
            serverCircleSmallFirst.setVisibility(VISIBLE);
        } else {
            serverCircleSmallFirst.setVisibility(GONE);
        }

        if (serversInCircles[2] != null) {
            serverCircleSmallSecond.setImageDrawable(serverDrawable(serversInCircles[2].name));
            serverCircleSmallSecond.setVisibility(VISIBLE);
        } else {
            serverCircleSmallSecond.setVisibility(GONE);
        }
    }

    @NonNull
    private ServerDrawable serverDrawable(String name) {
        return new ServerDrawable(name,
                ContextCompat.getColor(getContext(), R.color.drawer_header_circle_background),
                ContextCompat.getColor(getContext(), R.color.drawer_header_circle_foreground),
                CIRCLE_TEXT_PADDING_RATIO);
    }

    private void buildServerSelectionDrawerItems() {
        serverSelectionItems = new ArrayList<>(servers.size() + 2);
        for (int i=0; i<servers.size(); i++) {
            Server server = servers.get(i);
            PrimaryDrawerItem serverItem = new PrimaryDrawerItem()
                    .withName(server.name)
                    .withIdentifier(i)
                    .withDescription(server.host + (server.port != null ? ":" + server.port : ""))
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
                servers.indexOf(serversInCircles[0]) + 1);

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
