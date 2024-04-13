package co.aospa.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.qs.QSHost;
import com.android.systemui.plugins.qs.QSTile.BooleanState;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.R;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.qs.logging.QSLogger;
import com.android.systemui.qs.QsEventLogger;

import javax.inject.Inject;

public class AdblockTile extends QSTileImpl<BooleanState> {

    public static final String TILE_SPEC = "adblock";

    @Inject
    public AdblockTile(
            QSHost host,
            QsEventLogger uiEventLogger,
            @Background Looper backgroundLooper,
            @Main Handler mainHandler,
            FalsingManager falsingManager,
            MetricsLogger metricsLogger,
            StatusBarStateController statusBarStateController,
            ActivityStarter activityStarter,
            QSLogger qsLogger
    ) {
        super(host, uiEventLogger, backgroundLooper, mainHandler, falsingManager, metricsLogger,
                statusBarStateController, activityStarter, qsLogger);
    }

    @Override
    public BooleanState newTileState() {
        return new BooleanState();
    }

    @Override
    protected void handleClick(@Nullable View view) {
        String dnsMode = Settings.Global.getString(
            mContext.getContentResolver(),
            Settings.Global.PRIVATE_DNS_MODE
        );
        boolean isEnabled = "hostname".equals(dnsMode) && !dnsMode.isEmpty();
        dnsHandler(!isEnabled);
    }

    private void dnsHandler(boolean isChecked) {
        if (isChecked) {
            Settings.Global.putString(
                    mContext.getContentResolver(),
                    Settings.Global.PRIVATE_DNS_MODE,
                    "hostname"
            );
            Settings.Global.putString(
                    mContext.getContentResolver(),
                    Settings.Global.PRIVATE_DNS_SPECIFIER,
                    "dns.adguard.com"
            );
        } else {
            Settings.Global.putString(
                    mContext.getContentResolver(),
                    Settings.Global.PRIVATE_DNS_MODE,
                    "off"
            );
        }
        refreshState();
    }

    @Override
    public Intent getLongClickIntent() {
        return new Intent(Settings.ACTION_WIRELESS_SETTINGS);
    }

    @Override
    public CharSequence getTileLabel() {
        return mContext.getString(R.string.quick_settings_adblock_label);
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        state.label = getTileLabel();
        String dnsMode = Settings.Global.getString(
            mContext.getContentResolver(),
            Settings.Global.PRIVATE_DNS_MODE
        );
        if ("hostname".equals(dnsMode)) {
            state.icon = ResourceIcon.get(R.drawable.ic_qs_adblock_enabled);
            state.state = Tile.STATE_ACTIVE;
        } else {
            state.icon = ResourceIcon.get(R.drawable.ic_qs_adblock_disabled);
            state.state = Tile.STATE_INACTIVE;
        }
    }
}